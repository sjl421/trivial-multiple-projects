package com.charlie.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class AsyncServerHandler implements Runnable {
    public CountDownLatch latch;
    public AsynchronousServerSocketChannel schannel;

    public AsyncServerHandler(int port) {
        try {
            // 创建服务端通道
            schannel = AsynchronousServerSocketChannel.open();
            // 绑定端口
            schannel.bind(new InetSocketAddress(port));
            System.out.println("服务器已启动，端口号：" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // CountDownLatch 初始化
        // 它的作用：在完成一组在执行的操作之前，允许当前的现场一直阻塞
        // 此处可以让现场再次阻塞，防止服务端执行后退出
        latch = new CountDownLatch(1);
        // 接收客户端的连接
        schannel.accept(this, new AcceptHandler());
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Server shutdown");
    }
}
