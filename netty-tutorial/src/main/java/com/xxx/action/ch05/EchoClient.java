package com.xxx.action.ch05;

import com.xxx.action.ch07.MsgpackDecoder;
import com.xxx.action.ch07.MsgpackEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;


/**
 * Created by dhy on 2017/5/3.
 *
 */
public class EchoClient {
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024), new StringEncoder(), new StringDecoder(), new EchoClientHandler());
//                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
//                            ch.pipeline().addLast(new StringDecoder());
//                            ch.pipeline().addLast(new EchoClientHandler());

                            /**
                             * @param maxFrameLength            最大 frame 长度
                             * @param lengthFieldOffset         frame中length偏移量
                             * @param lengthFieldLength         length自身长度
                             * @param lengthAdjustment          在部分协议中，length除了包含body长度之外还包含head长度，用该变量与length相加得到实际长度
                             * @param initialBytesToStrip       通过该变量来调整跳过的长度，例如可以通过该变量跳过head来仅仅获取body
                             */
                            ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 3, 0,3));
                            ch.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
                            ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(3));
                            ch.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
                            ch.pipeline().addLast(new MsgpackEchoClientHandler(100));
                        }
                    });

            ChannelFuture f = b.connect(EchoServer.HOST, EchoServer.PORT).sync();

//            f.channel().disconnect();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
