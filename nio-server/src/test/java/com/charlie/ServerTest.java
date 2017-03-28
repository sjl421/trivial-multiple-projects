package com.charlie;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by dhy on 17-3-28.
 *
 */
public class ServerTest {
    @Test
    public void testServer() throws IOException {
        Server server = new Server(PORT);
        server.start();
        Client client = new Client(LOCALHOST, PORT);
    }

    private final static int PORT = 5100;

    private final static String LOCALHOST = "127.0.0.1";
}