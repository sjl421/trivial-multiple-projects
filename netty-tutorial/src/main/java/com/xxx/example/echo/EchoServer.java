package com.xxx.example.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by dhy on 2017/5/3.
 *
 */
public class EchoServer {
    static final int PORT = 8080;
    static final String HOST = "127.0.0.1";

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
//                    .handler(new ChannelInitializer<NioServerSocketChannel>() {
//                        @Override
//                        protected void initChannel(NioServerSocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new EchoServerHandler());
//                        }
//                    })
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024), new StringDecoder(), new StringEncoder(), new EchoServerHandler());
                        }
                    });
            // 绑定端口并且开始接受客户端的 accept 连接
            ChannelFuture f = b.bind(PORT).sync();

            System.out.println("Server start to shutdown!");
            // 关闭 Server，在本例子中由于上面的代码将一直阻塞，本行代码将永远不会执行
            ChannelFuture fu = f.channel().closeFuture().sync();
            System.out.println("Server shutdown successfully");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
