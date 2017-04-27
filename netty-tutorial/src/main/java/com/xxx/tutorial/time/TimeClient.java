package com.xxx.tutorial.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by charlie.du on 2017/4/27.
 *
 */
public class TimeClient {
    public static void main(String[] args) throws InterruptedException {
        String host;
        int port;

        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            host = "127.0.0.1";
            port = 8080;
        }

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();                                  // (1)
            b.group(workerGroup);                                           // (2)
            b.channel(NioSocketChannel.class);                              // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true);               // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();                 // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
