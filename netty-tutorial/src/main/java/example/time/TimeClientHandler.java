package example.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

/**
 * Created by dhy on 2017/5/4.
 *
 * 当客户端和服务端通过TCP链路建立成功之后，netty的NIO线程会调用 channelActive 方法，
 * 发送查询时间的指令给服务端，调用 ChannelHandlerContext 的 writeAndFlush 方法
 * 激昂请求消息发送给服务端；
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger Log = Logger.getLogger(TimeClientHandler.class.getName());

    private final ByteBuf firstMessage;

    public TimeClientHandler() {
        System.out.println("Time handler init!");
        //            QUERY TIME ORDER
        byte[] req = "QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client is active");
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("Now is : " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 释放资源
        Log.warning("Unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }
}
