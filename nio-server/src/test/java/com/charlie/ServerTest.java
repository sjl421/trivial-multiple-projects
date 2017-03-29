package com.charlie;

import com.charlie.domain.HttpMethod;
import com.charlie.domain.HttpRequest;
import com.charlie.domain.HttpResponse;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhy on 17-3-28.
 *
 */
public class ServerTest extends Thread {
    @Test
    public void testConcurrency() throws IOException {
        Server server = new Server(PORT);
        server.start();
        long start = System.currentTimeMillis();
        for (int i =  0; i < THREAD_COUNT; i++) {
            new ServerTest().start();
        }
        long end = System.currentTimeMillis();
        float timePerThread = ((float) (end - start)) / ((float) THREAD_COUNT);
        System.out.println(String.format("Total thread:[%d], time consume:[%d], time consume / per thread:[%f]", THREAD_COUNT, end-start, timePerThread));
    }

    @Override
    public void run() {
        try {
            Client client = new Client(LOCALHOST, PORT);
            HttpRequest request = new HttpRequest();
            HttpMethod httpMethod = randomHttpMethod();
            request.setHttpMethod(httpMethod);
            client.sendRequest(request);
            HttpResponse response = client.acceptResponse();
            assertEquals(response.getHttpMethod(), httpMethod);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpMethod randomHttpMethod() {
        return random.nextInt() % 2 == 0 ? HttpMethod.GET : HttpMethod.POST;
    }

    private Random random = new Random();
    private final static int PORT = 5100;
    private final static String LOCALHOST = "127.0.0.1";
    private final static int THREAD_COUNT = 5000;
}