package com.charlie.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "utf-8");
        System.out.println("服务器消息: " + body);
    }

    public boolean sendMsg(String msg) {
        System.out.println("客户端发送消息:" + msg);
        byte[] req = msg.getBytes();
        ByteBuf m = Unpooled.buffer(req.length);
        m.writeBytes(req);
        ctx.writeAndFlush(m);
        return !msg.equals("q");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
