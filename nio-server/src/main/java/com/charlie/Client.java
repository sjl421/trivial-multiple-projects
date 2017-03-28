package com.charlie;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by dhy on 17-3-28.
 *
 */
public class Client {

    private SocketChannel sc;

    public Client(String host, int port) throws IOException {
        sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress(host, port));
        sc.finishConnect();
    }
}
