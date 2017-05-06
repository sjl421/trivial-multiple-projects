package com.xxx.action.ch07;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * Created by dhy on 2017/5/5.
 * MessagePack编码器
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws IOException {
        MessagePack pack = new MessagePack();
        // Serialize
        byte[] raw = pack.write(msg);
        out.writeBytes(raw);
    }
}
