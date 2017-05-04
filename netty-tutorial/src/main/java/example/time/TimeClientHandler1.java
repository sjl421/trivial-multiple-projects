package example.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

/**
 * Created by dhy on 2017/5/4.
 * 由于这个例子没有考虑TCP的 粘包/拆包，我们的程序不能正常工作；
 */
public class TimeClientHandler1 extends ChannelInboundHandlerAdapter {
    private static final Logger Log = Logger.getLogger(TimeClientHandler1.class.getName());

    private int counter;

    private byte[] req;

    public TimeClientHandler1() {
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = new String(req, "UTF-8");
//        System.out.println("Now is : " + body + "; the counter is : " + ++counter);

        /*
         * 增加解码器后的read事件
         */
        String body = (String) msg;
        System.out.println("Now is : " + body + " ; the counter is : " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 释放资源
        Log.warning("Unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }
}
