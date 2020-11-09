package com.simit.netty.encode;

import com.simit.netty.Datagram;
import com.simit.netty.entity.exception.EmptyContentException;
import com.simit.netty.util.Converter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: ys xu
 * @Date: 2020/10/7 19:18
 * 编码handler，将Datagram对象转换为字节数据，发送到远端。
 */

@Component
public class Encoder extends MessageToMessageEncoder<Datagram> {
    private static final Logger logger = LoggerFactory.getLogger(Encoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Datagram datagram, List<Object> out) throws Exception {
        if(datagram == null){
            throw new EmptyContentException("Datagram is null");
        }


        int length = (datagram.getCommandCode().length() + datagram.getData().length()) / 2;
        datagram.setLength(String.format("%4s", Integer.toHexString(length).toUpperCase()).replace(" ", "0"));

        String preCS = datagram.getStart() + datagram.getType() + datagram.getId() + datagram.getControlCode() +
                datagram.getLength() + datagram.getCommandCode() + datagram.getData();

        datagram.setCs(String.format("%2s", Converter.calculateCS(preCS)).replace(" ", "0"));

        String msg = preCS + datagram.getCs() + datagram.getEnd();

        logger.info("send to client: " + msg);

        byte[] bytes = Converter.hex2Bytes(msg);
        ByteBuf buf = ctx.alloc().buffer(bytes.length);
        buf.writeBytes(bytes);

        out.add(new DatagramPacket(buf, datagram.getAddress()));
    }
}
