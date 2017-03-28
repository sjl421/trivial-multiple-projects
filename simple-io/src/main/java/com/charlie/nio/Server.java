package com.charlie.nio;

import java.io.IOException;

/**
 * Created by dhy on 17-3-27.
 * NIO创建Server
 */
public class Server {
    private static int DEFAULT_PORT = 12345;
    private static ServerHandle serverHandle;

    public static void start() throws IOException {
        start(DEFAULT_PORT);
    }

    public static synchronized void start(int port) throws IOException {
        if (serverHandle != null) {
            serverHandle.stop();
        }

        serverHandle = new ServerHandle(port);
        new Thread(serverHandle, "Server").start();
    }

    public static void main(String[] args) throws IOException {
        start();
    }
}
