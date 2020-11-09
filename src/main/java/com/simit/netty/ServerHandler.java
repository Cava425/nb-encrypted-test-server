package com.simit.netty;

import com.simit.data.BatchSumRepository;
import com.simit.data.EncryptResultRepository;
import com.simit.data.GasAddressRepository;
import com.simit.entity.AutoResult;
import com.simit.entity.BatchSum;
import com.simit.entity.EncryptResult;
import com.simit.entity.GasAddress;
import com.simit.netty.entity.field.*;
import com.simit.netty.util.Decryption;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ys xu
 * @Date: 2020/9/25 16:42
 */

@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Value("${field.data.manage-word}")
    private String manageWord;

    @Value("${field.data.upload-period}")
    private String uploadPeriod;

    @Value("${field.data.target-ip}")
    private String targetIp;

    @Value("${field.data.target-port}")
    private String targetPort;

    @Autowired
    private Decryption decryption;
    @Autowired
    private GasAddressRepository gasAddressRepository;
    @Autowired
    private BatchSumRepository batchSumRepository;
    @Autowired
    private EncryptResultRepository encryptResultRepository;

    // 命令索引与表号的键值对，向该表发送当前索引对应的命令
    private Map<String, String> index = new HashMap<>();

    private Datagram datagram;


    /**
     * @param ctx ChannelHandlerContext对象，负责数据在pipeline中传递
     * @param msg 数据对象，由pipeline中上一个handler传递过来的数据，此处是DatagramDecoder传递过来的Datagram对象
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof Datagram) {
            datagram = (Datagram) msg;

            // 根据命令码发送收到数据应答、告警应答、新网络参数确认应答
            if (datagram.isActive()) {
                switch (datagram.getCommandCode()) {
                    case "01":
                    case "51":
                        sendReceivedDatagramResponse(ctx, "8F");
                        break;
                    case "84":
                        sendReceivedDatagramResponse(ctx, "84");
                        break;
                    case "80":
                        sendReceivedDatagramResponse(ctx, "80");
                        break;
                    default:
                        logger.warn("未识别的上行数据包，无法发送收到数据应答");
                }
            }

            // 根据燃气表设备号获取加密插件测试结果表中的部分信息，批次号、抽测标记等
            EncryptResult encryptResult = encryptResultRepository.findOne(datagram.getId());

            // 如果数据库中有对应燃气表设备号的记录，且上行数据包类型不是80和84
            if (encryptResult != null
                    && !"80".equals(datagram.getCommandCode())
                    && !"84".equals(datagram.getCommandCode())) {

                // 初始化命令索引序列
                String i = index.getOrDefault(datagram.getId(), "1");

                switch (i) {
                    case "1":
                        if (datagram.isActive()
                                && "51".equals(datagram.getCommandCode())
                                && "01".equals(datagram.getCommandCode())) {
                            // 下发升级加密插件指令
                            sendUpdateEncryptedPluginCommand(ctx);
                            index.put(datagram.getId(), "2");
                            break;
                        }
                    case "2":
                        if (!datagram.isActive() && "82".equals(datagram.getCommandCode())) {
                            // 发送结束报文
                            sendEndPacketCommand(ctx);
                            index.put(datagram.getId(), "3");
                            break;

                        }
                    case "3":
                        if (datagram.isActive() && "51".equals(datagram.getCommandCode())) {
                            // 结果写入到数据库
                            updateResult(encryptResult);
                            // 切换到燃气正式服务器
                            sendModifiedNetParamCommand(ctx);
                            // 测试结束，将索引移除
                            index.remove(datagram.getId());
                            break;
                        }
                    default:
                        // 发送结束报文
                        if (datagram.isActive() && "51".equals(datagram.getCommandCode())) {
                            logger.info("收到不在测试序列中的数据包类型：{}", datagram.getCommandCode());
                            sendEndPacketCommand(ctx);
                            index.remove(datagram.getId());
                        }
                }
            }else {
                if (!datagram.isActive() && "81".equals(datagram.getCommandCode())) {
                    logger.info("切换燃气正式服务器命令执行: {}", datagram.isSuccessful() ? "成功" : "失败");
                } else if (datagram.isActive() && "51".equals(datagram.getCommandCode())) {
                    logger.info("非抽测燃气表，发送切换到正式服务器命令。");
                    sendModifiedNetParamCommand(ctx);
                }
            }
        }
    }


    private void updateResult(EncryptResult encryptResult){
        // 更新命令执行结果，即常规上报中控制码字段的标记
        encryptResult.setFlag(Integer.valueOf(datagram.getControlCode().substring(0, 2), 16));
        encryptResult.setOriginData(datagram.toString());
        encryptResult.setTs(System.currentTimeMillis());
        encryptResultRepository.save(encryptResult);

        // 更新t_batch表中的统计数据
        BatchSum batchSum = batchSumRepository.findOne(encryptResult.getBatchId(), String.valueOf(encryptResult.getSamplingFlag()));
        if ("00FFFFFF".equals(datagram.getControlCode()) || "20FFFFFF".equals(datagram.getControlCode())) {
            batchSum.setSuccessfulNum(batchSum.getSuccessfulNum() + 1);
        } else {
            batchSum.setFailureNum(batchSum.getFailureNum() + 1);
        }
        batchSum.setTotalNum(batchSum.getTotalNum() + 1);
        batchSumRepository.save(batchSum);
    }

    /**
     * 判断自动化测试结果中的每条命令是否全部通过
     *
     * @param results
     * @return
     */
    private boolean isPass(List<AutoResult> results) {
        for (AutoResult result : results) {
            if (!result.getSuccessfulCode()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 发送收到上行数据的应答
     * 常规上报  --->  发送8F应答
     * 异常告警  --->  发送84应答
     * 新网络参数修改确认  --->   发送80应答
     *
     * @param ctx
     * @param commandCode
     * @throws CloneNotSupportedException
     */
    private void sendReceivedDatagramResponse(ChannelHandlerContext ctx, String commandCode) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        dg.setControlCode((datagram.isCiphertext() ? "A0" : "80")
                + datagram.getControlCode().substring(2));
        dg.setCommandCode(commandCode);
        dg.setData(datagram.getRandNum());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg = decryption.encrypt(dg);
        }
        ctx.writeAndFlush(dg);
    }

    /**
     * 发送更新加密插件命令
     *
     * @param ctx
     * @throws CloneNotSupportedException
     */
    private void sendUpdateEncryptedPluginCommand(ChannelHandlerContext ctx) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        dg.setControlCode("00FFFFFF");
        dg.setCommandCode("82");
        dg.setData(datagram.getRandNum());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg.setControlCode("20FFFFFF");
            dg = decryption.encrypt(dg);
        }
        ctx.writeAndFlush(dg);
    }

    /**
     * 发送结束报文命令
     *
     * @param ctx
     * @throws CloneNotSupportedException
     */
    private void sendEndPacketCommand(ChannelHandlerContext ctx) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        dg.setControlCode("00FFFFFF");
        dg.setCommandCode("00".equals(datagram.getType()) ? "0F" : "03".equals(datagram.getType()) ? "5F" : "FF");
        dg.setData(new EndMessage(manageWord, uploadPeriod).getEndMessage());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg.setControlCode("20FFFFFF");
            dg = decryption.encrypt(dg);
        }
        ctx.writeAndFlush(dg);
    }

    /**
     * 发送修改网络参数命令
     *
     * @param ctx
     * @throws CloneNotSupportedException
     */
    private void sendModifiedNetParamCommand(ChannelHandlerContext ctx) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        GasAddress gasAddress = gasAddressRepository.findOne();
        if (gasAddress != null) {
            String[] address = gasAddress.getFormalDomain().split(":");
            if (address.length == 2) {
                targetIp = address[0];
                targetPort = address[1];
            }
        }
        dg.setControlCode("00FFFFFF");
        dg.setCommandCode("81");
        dg.setData(new ModifiedNetParam(targetIp, targetPort, datagram.getRandNum()).getModifiedNetParam());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg.setControlCode("20FFFFFF");
            dg = decryption.encrypt(dg);
        }
        ctx.writeAndFlush(dg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
