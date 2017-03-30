package com.xxx;

import com.xxx.domain.HttpMethod;
import com.xxx.domain.HttpRequest;
import com.xxx.domain.HttpResponse;
import com.xxx.event.LogEvent;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class ClientTest {

    @Test
    public void testSendRequest() throws IOException {
        Server server = new Server(PORT);
        server.start();
        Client client = new Client(LOCALHOST, PORT);
        LogEvent logEvent = new LogEvent();
        DefaultEventHandler.INSTANCE.addListener(logEvent);
        HttpRequest request = new HttpRequest();
        HttpMethod httpMethod = randomHttpMethod();
        request.setRequestURl("http://localhost:8080/");
        request.setHttpMethod(httpMethod);
        client.sendRequest(request);
        HttpResponse response = client.acceptResponse();
        assertEquals(response.getHttpMethod(), httpMethod);
    }

    private HttpMethod randomHttpMethod() {
        return random.nextInt() % 2 == 0 ? HttpMethod.GET : HttpMethod.POST;
    }

    private Random random = new Random();
    private final static int PORT = 5100;
    private final static String LOCALHOST = "127.0.0.1";
}
