package com.charlie.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler> {
    @Override
    public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
        // 继续接收其他客户端的请求
        AioServer.clientCount++;
        System.out.println("连接的客户端数量：" + AioServer.clientCount);
        serverHandler.schannel.accept(serverHandler, this);
        // 创建新的 Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 异步读取
        channel.read(buffer, buffer, new ServerReadHandler(channel));
    }

    @Override
    public void failed(Throwable exc, AsyncServerHandler serverHandler) {
        exc.printStackTrace();
        serverHandler.latch.countDown();
    }
}
