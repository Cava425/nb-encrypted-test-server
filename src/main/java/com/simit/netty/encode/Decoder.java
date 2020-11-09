package com.simit.netty.encode;

import com.simit.netty.entity.exception.DataFieldContentException;
import com.simit.netty.util.Converter;
import com.simit.netty.Datagram;
import com.simit.netty.util.Decryption;
import com.simit.netty.util.Verification;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: ys xu
 * @Date: 2020/9/25 16:33
 */

@Component
public class Decoder extends MessageToMessageDecoder<DatagramPacket> {

    private static final Logger logger = LoggerFactory.getLogger(Decoder.class);

    private volatile static AtomicInteger count = new AtomicInteger(1); // 计数

    @Autowired
    Decryption decryption;

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {

        ByteBuf buf = packet.content();
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);

        String body = Converter.bytes2HexString(data);
        logger.info("count: " + count.getAndIncrement());
        logger.info("received from client: " + body);


        Datagram datagram;
        try {

            Verification.verify(body);

            datagram = new Datagram();
            datagram.setType(body.substring(2, 4));
            datagram.setId(body.substring(4, 16));
            datagram.setControlCode(body.substring(16, 24));
            datagram.setLength(body.substring(24, 28));
            datagram.setCommandCode(body.substring(28, 30));
            datagram.setData(body.substring(30, body.length() - 4));
            datagram.setCs(body.substring(body.length() - 4, body.length() - 2));


            datagram.setAddress(packet.sender());

            datagram.setPugid("");
            datagram.setCiphertext(Converter.hex2Binary(datagram.getControlCode()).charAt(2) == '0' ? false : true);
            datagram.setActive(Converter.hex2Binary(datagram.getControlCode()).charAt(0) == '1' ? false : true);
            datagram.setSuccessful(Converter.hex2Binary(datagram.getControlCode()).charAt(1) == '1' ? false : true);

            if (datagram.isCiphertext()) {
                datagram = decryption.decrypt(datagram);
            }

            datagram.setRandNum(calculateRandNum(datagram.toString()));


        } catch (Exception e) {
            logger.warn("Decoding datagram failure, Maybe some data fields have errors", e);
            datagram = null;
        }

        try {
            out.add(datagram);
        } catch (NullPointerException e) {
            logger.warn("Decoding datagram failure, added String Object with \"\"", e);
            out.add("");
        }
    }

    private String calculateRandNum(String body) {
        String data = body.substring(30, body.length() - 4);
        String commandCode = body.substring(28, 30);
        String randNum = "";
        switch (commandCode) {
            case "01":
                if ((data.length() - 52) % 36 != 0) {
                    throw new DataFieldContentException("数据域字段异常");
                }
                randNum = data.substring(38, 46);
                break;
            case "51":
                if (((data.length() - 52) % 68) != 0) {
                    throw new DataFieldContentException("数据域字段异常");
                }
                randNum = data.substring(38, 46);
                break;
            case "84":
            case "85":
                if ((data.length() - 10) % 16 != 0) {
                    throw new DataFieldContentException("数据域字段异常");
                }
                randNum = data.substring(data.length() - 8);
                break;
            case "80":
            case "82":
            case "83":
            case "88":
                if (data.length() != 8) {
                    throw new DataFieldContentException("数据域字段异常");
                }
                randNum = data.substring(data.length() - 8);
                break;
            case "81":
                if (data.length() != 108) {
                    throw new DataFieldContentException("数据域字段异常");
                }
                randNum = data.substring(data.length() - 24, data.length() - 16);
                break;
            case "86":
            case "87":
            case "8D":
                if (data.length() != 10) {
                    throw new DataFieldContentException("数据域字段异常");
                }
                randNum = data.substring(data.length() - 8);
                break;
            case "8E":
                if (data.length() != 130) {
                    throw new DataFieldContentException("数据域字段异常");
                }
                randNum = data.substring(data.length() - 8);
                break;
            case "A1":
            case "A2":
                if (data.length() != 44) {
                    throw new DataFieldContentException("数据域字段异常");
                }
                randNum = data.substring(data.length() - 8);
                break;
            default:
        }

        return randNum;
    }
}
