package com.simit.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simit.data.BatchRecordRepository;
import com.simit.data.GasAddressRepository;
import com.simit.data.ResultRepository;
import com.simit.data.SamplingSumRepository;
import com.simit.entity.BatchRecord;
import com.simit.entity.GasAddress;
import com.simit.entity.Result;
import com.simit.entity.SamplingSum;
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
    private SamplingSumRepository samplingSumRepository;
    @Autowired
    private BatchRecordRepository batchRecordRepository;
    @Autowired
    private ResultRepository resultRepository;

    // 命令索引与表号的键值对，向该表发送当前索引对应的命令
    private Map<String, Integer> index = new HashMap<>();

    private Datagram datagram;


    /**
     * @param ctx ChannelHandlerContext对象，负责数据在pipeline中传递
     * @param object 数据对象，由pipeline中上一个handler传递过来的数据，此处是DatagramDecoder传递过来的Datagram对象
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {

        if (object instanceof Datagram) {
            datagram = (Datagram) object;

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
            BatchRecord batchRecord = batchRecordRepository.find(datagram.getId());

            // 如果数据库中有对应燃气表设备号的记录，且上行数据包类型不是80和84
            if (batchRecord != null && batchRecord.getIsTested() == null
                    && !"80".equals(datagram.getCommandCode())
                    && !"84".equals(datagram.getCommandCode())) {

                // 初始化命令索引序列
                int i = index.getOrDefault(datagram.getId(), 1);
                Map<String, Object> data = new HashMap<>();
                Map<String, Object> msg = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                switch (i) {
                    case 1:
                        if (datagram.isActive()
                                && ("51".equals(datagram.getCommandCode())
                                || "01".equals(datagram.getCommandCode()))) {
                            sendUpdateEncryptedPluginCommand(ctx);
                            index.put(datagram.getId(), 2);
                            break;
                        }
                    case 2:
                        if (!datagram.isActive() && "82".equals(datagram.getCommandCode())) {
                            data.put("code", datagram.isSuccessful());
                            data.put("msg", msg);
                            Result result = new Result(datagram.getId(), batchRecord.getBatchId(), batchRecord.getSamplingFlag(), datagram.toString(), datagram.getCommandCode(), "升级加密插件应答", datagram.getControlCode(), mapper.writeValueAsString(data), System.currentTimeMillis());
                            resultRepository.save(result);
                            EndMessage message = new EndMessage("0000", "000A");
                            sendEndPacketCommand(ctx, message);
                            index.put(datagram.getId(), 3);
                            break;
                        }
                    case 3:
                        if (datagram.isActive() && ("51".equals(datagram.getCommandCode())
                                || "01".equals(datagram.getCommandCode()))) {
                            // 结果写入到数据库
                            data.put("code", datagram.isSuccessful());
                            data.put("msg", msg);
                            Result result = new Result(datagram.getId(), batchRecord.getBatchId(), batchRecord.getSamplingFlag(), datagram.toString(), datagram.getCommandCode(), "升级加密插件标记", datagram.getControlCode(), mapper.writeValueAsString(data), System.currentTimeMillis());
                            resultRepository.save(result);
                            sendRemoteCloseValveEnable(ctx);
                            index.put(datagram.getId(), 4);
                            break;
                        }
                    case 4:
                        if(!datagram.isActive() && "88".equals(datagram.getCommandCode())) {
                            // 结果写入到数据库
                            data.put("code", datagram.isSuccessful());
                            data.put("msg", msg);
                            Result result = new Result(datagram.getId(), batchRecord.getBatchId(), batchRecord.getSamplingFlag(), datagram.toString(), datagram.getCommandCode(), "远程关阀使能", datagram.getControlCode(), mapper.writeValueAsString(data), System.currentTimeMillis());
                            resultRepository.save(result);
                            sendRemoteCloseValve(ctx);
                            index.put(datagram.getId(), 5);
                            break;
                        }
                    case 5:
                        if(!datagram.isActive() && "87".equals(datagram.getCommandCode())) {
                            data.put("code", datagram.isSuccessful());
                            data.put("msg", msg);
                            Result result = new Result(datagram.getId(), batchRecord.getBatchId(), batchRecord.getSamplingFlag(), datagram.toString(), datagram.getCommandCode(), "远程关阀", datagram.getControlCode(), mapper.writeValueAsString(data), System.currentTimeMillis());
                            resultRepository.save(result);
                            sendRemoteOpenValve(ctx);
                            index.put(datagram.getId(), 6);
                            break;
                        }
                    case 6:
                        if(!datagram.isActive() && "86".equals(datagram.getCommandCode())) {
                            data.put("code", datagram.isSuccessful());
                            data.put("msg", msg);
                            Result result = new Result(datagram.getId(), batchRecord.getBatchId(), batchRecord.getSamplingFlag(), datagram.toString(), datagram.getCommandCode(), "远程开阀", datagram.getControlCode(), mapper.writeValueAsString(data), System.currentTimeMillis());
                            resultRepository.save(result);
                            sendResetSettings(ctx);
                            index.put(datagram.getId(), 7);
                            break;
                        }
                    case 7:
                        if (!datagram.isActive() && "8E".equals(datagram.getCommandCode())) {
                            data.put("code", datagram.isSuccessful());
                            data.put("msg", msg);
                            Result result = new Result(datagram.getId(), batchRecord.getBatchId(), batchRecord.getSamplingFlag(), datagram.toString(), datagram.getCommandCode(), "恢复出厂设置", datagram.getControlCode(), mapper.writeValueAsString(data), System.currentTimeMillis());
                            resultRepository.save(result);

                            // 在发送修改网络参数之前，检查之前的5条指令，如果全部成功发送网络修改参数
                            // 否则，把该表的记录移动到失败表中
                            List<Result> autoResults = resultRepository.findAll(datagram.getId());
                            if (isPass(autoResults) && autoResults.size() == 6) {
                                logger.info("前六条命令执行成功");
                                sendModifiedNetParamCommand(ctx);
                                index.put(datagram.getId(), 8);
                            } else {
                                logger.info("前六条命令未执行成功, 结果保存到失败数据表中");
                                resultRepository.saveToFailed(autoResults);
                                resultRepository.delete(autoResults);

                                logger.info("移除测试索引, 重新开始测试计划");
                                index.remove(datagram.getId());
                            }
                            break;
                        }
                    case 8:
                        if(!datagram.isActive() && "81".equals(datagram.getCommandCode())) {
                            data.put("code", datagram.isSuccessful());
                            data.put("msg", msg);
                            Result result = new Result(datagram.getId(), batchRecord.getBatchId(), batchRecord.getSamplingFlag(), datagram.toString(), datagram.getCommandCode(), "修改网络参数", datagram.getControlCode(), mapper.writeValueAsString(data), System.currentTimeMillis());
                            resultRepository.save(result);

                            List<Result> autoResults = resultRepository.findAll(datagram.getId());

                            // 修改网络参数命令执行成功，发送一条一天上报一次的结束报文
                            if(datagram.isSuccessful()){
                                // 发送结束报文
                                EndMessage message = new EndMessage("0000", "05A0");
                                sendEndPacketCommand(ctx, message);

                                // 将所有的7条测试命令的数据全部写道成功的表中
                                logger.info("所有命令执行成功, 结果保存到成功数据表中");
                                resultRepository.saveToSuccessful(autoResults);
                            }else {
                                // 将所有的7条测试命令（6成功 1失败）的数据全部写道失败的表中
                                logger.info("修改网络参数命令执行失败, 结果保存到失败数据表中");
                                resultRepository.saveToFailed(autoResults);
                            }

                            // 从当前测试表中删除数据
                            resultRepository.delete(autoResults);

                            // 将表号从commandsIndex中移除
                            index.remove(datagram.getId());
                            break;
                        }
                    default:
                        // 如果非序列上报不是常规数据上报
                        logger.info("非序列上报, 上报数据命令码为: {}", datagram.getCommandCode());
                        // 将上报数据以及未应答指令名称写道数据库
                        data.put("code", "false");
                        data.put("msg", msg);
                        Result result = new Result(datagram.getId(), batchRecord.getBatchId(), batchRecord.getSamplingFlag(), datagram.toString(), datagram.getCommandCode(), indexConvertToCommandNames(i - 1), datagram.getControlCode(), mapper.writeValueAsString(data), System.currentTimeMillis());
                        resultRepository.save(result);

                        String code = datagram.getCommandCode();
                        // 测试过程中，触发常规上报，跳过该指令，继续进行测试
                        if ("01".equals(code) || "51".equals(code)) {
                            // 跳过该指令
                            sendNextCommandByIndex(i, ctx);
                        }else {
                            // 更新数据库中当前在测数据
                            logger.info("清空当前在测数据表，测试结果写道测试失败表中，重新执行测试计划");
                            updateResult(batchRecord);
                            index.remove(datagram.getId());
                        }
                }
            }else {
                if(batchRecord != null && batchRecord.getIsTested() != null){
                    logger.info("燃气表已经测试");
                }

                if (!datagram.isActive() && "81".equals(datagram.getCommandCode())) {
                    logger.info("修改网络参数命令执行: {}", datagram.isSuccessful() ? "成功" : "失败");
                } else if (datagram.isActive() && "51".equals(datagram.getCommandCode())) {
                    // 切换到燃气正式服务器
                    sendModifiedNetParamCommand(ctx);
                }

                // 清空在测表中该燃气表的数据
                List<Result> results = resultRepository.findAll(datagram.getId());
                resultRepository.delete(results);
            }

        }
    }

    /**
     * 将索引值与命令名称对应起来
     * @param index 传入当前索引值 - 1
     * @return 返回未收到应答的指令
     */
    private String indexConvertToCommandNames(int index){
        String name = "未知命令名称";
        switch (index){
            case 0:
                name = "常规数据上报";
                break;
            case 1:
                name = "读取安全流量参数";
                break;
            case 2:
                name = "设置安全流量参数";
                break;
            case 3:
                name = "读取事件记录";
                break;
            case 4:
                name = "远程关阀使能";
                break;
            case 5:
                name = "远程关阀";
                break;
            case 6:
                name = "远程开阀";
                break;
            case 7:
                name = "恢复出厂设置";
                break;
            case 8:
                name = "修改网络参数";
                break;
        }
        return name;
    }

    private void sendNextCommandByIndex(int i, ChannelHandlerContext ctx) throws CloneNotSupportedException {
        switch (i){
            case 1:
                break;
            case 2:
                logger.info("未收到更新加密插件的应答，发送结束报文");
                EndMessage message = new EndMessage("0000", "000A");
                sendEndPacketCommand(ctx, message);
                index.put(datagram.getId(), 3);
                break;
            case 3:
                logger.info("未收到常规数据上报，发送远程关阀使能命令");
                sendRemoteCloseValveEnable(ctx);
                index.put(datagram.getId(), 4);
                break;
            case 4:
                logger.info("未收到远程关阀使能的应答，发送远程关阀命令");
                sendRemoteCloseValve(ctx);
                index.put(datagram.getId(), 5);
                break;
            case 5:
                logger.info("未收到远程关阀的应答，发送远程开阀命令");
                sendRemoteOpenValve(ctx);
                index.put(datagram.getId(), 6);
                break;
            case 6:
                logger.info("未收到远程开阀的应答，发送恢复出厂设置命令");
                sendResetSettings(ctx);
                index.put(datagram.getId(), 7);
                break;
            case 7:
                List<Result> autoResults = resultRepository.findAll(datagram.getId());
                resultRepository.saveToFailed(autoResults);
                resultRepository.delete(autoResults);
                logger.info("暂时移除命令索引，重新开始测试计划");
                index.remove(datagram.getId());
            default:
                logger.info("未知非序列上报, 命令序列索引:{}", i);
        }
    }

    private void updateResult(BatchRecord batchRecord){
        List<Result> results = resultRepository.findAll(datagram.getId());
        SamplingSum samplingSum = samplingSumRepository.findOne(batchRecord.getBatchId(), String.valueOf(batchRecord.getSamplingFlag()));
        if (isPass(results) && results.size() == 7) {
            logger.info("All commands pass, save results to successful table.");
            resultRepository.saveToSuccessful(results);
            samplingSum.setSuccessfulNum(samplingSum.getSuccessfulNum() + 1);
        } else {
            logger.info("Not all commands pass, save results to failed table.");
            resultRepository.saveToFailed(results);
            samplingSum.setFailureNum(samplingSum.getFailureNum() + 1);
        }
        resultRepository.delete(results);

        samplingSum.setTs(System.currentTimeMillis());
        samplingSumRepository.save(samplingSum);

        batchRecord.setIsTested("tested");
        batchRecordRepository.save(batchRecord);
    }

    /**
     * 判断自动化测试结果中的每条命令是否全部通过
     *
     * @param results
     * @return
     */
    private boolean isPass(List<Result> results) {
        for (Result result : results) {
            if (!result.getSuccessfulCode().contains("true")) {
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
        logger.info("发送收到数据应答数据包");
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
        logger.info("发送升级加密插件命令");
        ctx.writeAndFlush(dg);
    }

    /**
     * 发送结束报文命令
     *
     * @param ctx
     * @throws CloneNotSupportedException
     */
    private void sendEndPacketCommand(ChannelHandlerContext ctx, EndMessage message) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        dg.setControlCode("00FFFFFF");
        dg.setCommandCode("00".equals(datagram.getType()) ? "0F" : "03".equals(datagram.getType()) ? "5F" : "FF");
        dg.setData(message.getEndMessage());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg.setControlCode("20FFFFFF");
            dg = decryption.encrypt(dg);
        }
        logger.info("发送结束报文");
        ctx.writeAndFlush(dg);
    }


    /**
     * 发送远程关阀使能命令
     * @param ctx
     * @throws CloneNotSupportedException
     */
    private void sendRemoteCloseValveEnable(ChannelHandlerContext ctx) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        dg.setControlCode("00FFFFFF");
        dg.setCommandCode("88");
        dg.setData(new RemoteCloseValveEnable(datagram.getRandNum()).getRemoteCloseValveEnable());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg.setControlCode("20FFFFFF");
            dg = decryption.encrypt(dg);
        }
        logger.info("发送远程关阀使能命令");
        ctx.writeAndFlush(dg);
    }

    /**
     * 发送远程关阀命令
     * @param ctx
     * @throws CloneNotSupportedException
     */
    private void sendRemoteCloseValve(ChannelHandlerContext ctx) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        dg.setControlCode("00FFFFFF");
        dg.setCommandCode("87");
        dg.setData(new RemoteCloseValve(datagram.getRandNum()).getRemoteCloseValve());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg.setControlCode("20FFFFFF");
            dg = decryption.encrypt(dg);
        }
        logger.info("发送远程关阀命令");
        ctx.writeAndFlush(dg);
    }

    /**
     * 发送远程开阀命令
     * @param ctx
     * @throws CloneNotSupportedException
     */
    private void sendRemoteOpenValve(ChannelHandlerContext ctx) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        dg.setControlCode("00FFFFFF");
        dg.setCommandCode("86");
        dg.setData(new RemoteOpenValve((datagram.getRandNum())).getRemoteCloseValve());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg.setControlCode("20FFFFFF");
            dg = decryption.encrypt(dg);
        }
        logger.info("发送远程开阀命令");
        ctx.writeAndFlush(dg);
    }

    /**
     * 发送恢复出厂设置命令
     * @param ctx
     * @throws CloneNotSupportedException
     */
    private void sendResetSettings(ChannelHandlerContext ctx) throws CloneNotSupportedException {
        Datagram dg = (Datagram) datagram.clone();
        dg.setControlCode("00FFFFFF");
        dg.setCommandCode("8E");
        dg.setData(datagram.getRandNum());
        if (datagram.isCiphertext() && !datagram.getPugid().equals("")) {
            dg.setControlCode("20FFFFFF");
            dg = decryption.encrypt(dg);
        }
        logger.info("发送恢复出厂设置命令");
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
        logger.info("发送修改网络参数命令");
        ctx.writeAndFlush(dg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
