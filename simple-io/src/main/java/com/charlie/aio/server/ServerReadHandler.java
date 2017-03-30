package com.charlie.aio.server;

import com.charlie.nio.Calculator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class ServerReadHandler implements CompletionHandler<Integer, ByteBuffer> {

    // 用于读取半包消息和发送应答
    private AsynchronousSocketChannel channel;

    public ServerReadHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    // 读取到消息后的处理
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        // flip操作
        attachment.flip();
        byte[] message = new byte[attachment.remaining()];
        // TODO 观察这个位置在get的时候是否会改变attachment中的position等
        attachment.get(message);
        try {
            String expression = new String(message, "UTF-8");
            System.out.println("服务器收到消息：" + expression);
            String cal;
            try {
                cal = Calculator.cal(expression).toString();
            } catch (Exception e) {
                cal = "计算错误：" + e.getMessage();
            }
            // 向客户端发送消息
            doWrite(cal);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void doWrite(String result) {
        byte[] bytes = result.getBytes();
        final ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        // TODO 观察这个位置在put的时候是否会改变attachment中的position等
        writeBuffer.put(bytes);
        writeBuffer.flip();
        // 异步写数据，参数与前面的read一样
        channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                // 如果没有发送完，就继续发送直到完成
                if (buffer.hasRemaining()) {
                    channel.write(buffer, buffer, this);
                } else {
                    // 创建新的Buffer
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    // 异步读，第三个参数为接收消息回调的业务Handler
                     channel.read(readBuffer, readBuffer, new ServerReadHandler(channel));
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    channel.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
