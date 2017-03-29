package com.charlie.nio;


import javax.script.ScriptException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by dhy on 17-3-27.
 * NIO 服务端
 */
public class ServerHandler implements Runnable {

    // 多路复用器
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private volatile boolean started;

    public ServerHandler(int port) {
        try {
            // 创建多路复用器
            selector = Selector.open();
            // 打开监听的服务端通道
            serverChannel = ServerSocketChannel.open();
            // 如果为true，则此通道是阻塞模式；如果为false，则为非阻塞模式
            serverChannel.configureBlocking(false);
            // 绑定端口
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            // 监听客户端连接请求
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 标记服务器已开启
            started = true;
            System.out.println("服务器已经启动,端口号：" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        // 循环遍历selector
        while (started) {
            try {
                // 无论是否有读写事件发生，selector每隔1秒被唤醒一次
                selector.select(1000);
                // select方法是阻塞方法，只有当至少一个注册的事件发生的时候才会继续
                selector.select();
                // 返回select的所有键
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key;

                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 处理新接入的请求消息
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                // 通过ServerSocketChannel 的 accept 创建 SocketChannel 实例
                // 完成该操作意味着完成TCP三次握手，TCP物理链路正式建立
                SocketChannel sc = ssc.accept();
                // 设置为非阻塞的
                sc.configureBlocking(false);
                // 注册为读
                sc.register(selector, SelectionKey.OP_READ);
            }
        }
        // 读消息
        if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            // 创建ByteBuffer，并开辟一个1MB的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 读取请求码流，返回读取到的字节数
            int readBytes = sc.read(buffer);
            // 读取到字节，对字节进行编码解码
            if (readBytes > 0) {
                // 将缓冲区当前的limit设置为position=0，用于后续对缓冲区的读取操作
                buffer.flip();
                // 根据缓冲区刻度字节创建字节数组
                byte[] bytes = new byte[buffer.remaining()];
                // 将缓冲区可读字节数组复制到新建的数组中
                buffer.get(bytes);
                String expression = new String(bytes, "UTF-8");
                System.out.println("服务器接受到消息：" + expression);
                // 处理数据
                String result = null;
                try {
                    result = Calculator.cal(expression).toString();
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
                doWrite(sc, result);
            } else if (readBytes == 0) {
                // 没有读取到字节,忽略
                System.out.println("no input");
            } else if (readBytes < 0) {
                // 链路已经关闭，释放资源
                key.cancel();
                sc.close();
            }
        }
    }

    // 异步发送应答消息
    private void doWrite(SocketChannel channel, String response) throws IOException {
        // 将消息编码为字节数组
        byte[] bytes = response.getBytes();
        // 根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        // 将字节数组复制到缓冲区
        writeBuffer.put(bytes);
        // flip 操作
        writeBuffer.flip();
        // 发送缓冲区的字节数组
        channel.write(writeBuffer);
        // ****此处不包含 “写半包” 的代码
    }

    public void stop() throws IOException {
        started = false;
        if (selector!=null && selector.isOpen()) {
            selector.close();
        }
        if (serverChannel!=null && serverChannel.isOpen()) {
            serverChannel.close();
        }
    }
}
