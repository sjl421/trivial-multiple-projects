package com.charlie;

import com.charlie.event.EventHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by dhy on 17-3-28.
 * 服务器，在该类中需要初始化 ServerSocketChannel, 并处理客户端的连接请求，读请求等；
 */
public class Server extends Thread {

    private final ServerSocketChannel ssc;
    private final Selector selector;
    private EventHandler handler;

    public Server(int port) throws IOException {
        this(port, DefaultEventHandler.INSTANCE);
    }

    public Server(int port, EventHandler handler) throws IOException {
        this.handler = handler;
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(port));

        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
//                    if (!key.isValid()) {
//                        handler.executeOnError();
//                    }
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssc0 = (ServerSocketChannel) key.channel();
                        handler.executeOnAccept();

                        SocketChannel sc = ssc0.accept();
                        sc.configureBlocking(false);

                        handler.executeOnAccepted();
                        sc.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {

                    }
                }
            } catch (IOException e) {
                handler.executeOnError("Server got a error : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5100);
        server.start();
        Client client = new Client("127.0.0.1", 5100);
    }
}