package com.charlie;

import com.charlie.domain.HttpMethod;
import com.charlie.domain.HttpRequest;
import com.charlie.domain.HttpResponse;
import com.charlie.event.EventHandler;
import com.charlie.io.Reader;
import com.charlie.io.Writer;

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
                        handler.executeOnAccept(ssc0.getLocalAddress().toString());

                        SocketChannel sc = ssc0.accept();
                        sc.configureBlocking(false);

                        handler.executeOnAccepted(ssc0.getLocalAddress().toString());
                        sc.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        HttpRequest httpRequest = Reader.processRequest(sc);
                        sc.register(selector, SelectionKey.OP_WRITE, httpRequest);
                    } else if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        HttpRequest request = (HttpRequest) key.attachment();
                        Writer.processResponse(sc, request);
                    }
                }
            } catch (IOException e) {
                handler.executeOnError("Server got a error : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
    }
}
