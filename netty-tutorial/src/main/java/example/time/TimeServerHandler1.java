package example.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by dhy on 2017/5/4.
 * 模拟故障场景，使用netty的半包解码器来解决TCP粘包/拆包的问题
 * 每读到一条消息，就计数一次，然后发送应答消息给客户端。按照设计，服务端接收到的消息总数
 * 应该跟客户端发送的消息总数相同，而且请求消息删除回车换行符之后应该为 "QUERY TIME ORDER"
 */
public class TimeServerHandler1 extends ChannelInboundHandlerAdapter {
    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8").substring(0, req.length - System.getProperty("line.separator").length());
        System.out.println("The time server receive order : " + body + " ; the counter is : " + ++counter);
        String currentTime = body.startsWith("QUERY TIME ORDER") ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
