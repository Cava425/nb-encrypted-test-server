package com.simit.netty;

import com.simit.netty.encode.Decoder;
import com.simit.netty.encode.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @Author: ys xu
 * @Date: 2020/9/25 16:19
 */

@Component
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private Decoder decoder;

    @Autowired
    private Encoder encoder;

    @Autowired
    private ServerHandler serverHandler;

    public void start(int port) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .option(ChannelOption.SO_RCVBUF, 2048 * 2048)
                    .option(ChannelOption.SO_SNDBUF, 2048 * 2048)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(decoder);
                            pipeline.addLast(encoder);
                            pipeline.addLast(serverHandler);
                        }
                    });

            logger.info("Server start listen at " + port);
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
