package com.xxx.io;

import com.xxx.domain.HttpMethod;
import com.xxx.domain.HttpRequest;
import com.xxx.domain.HttpResponse;
import com.xxx.util.SerializeUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class Writer {

    public static HttpResponse processResponse(SocketChannel sc, HttpRequest request) throws ServerException {
        try {
            return service.submit(() -> write(sc, request)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpResponse write(SocketChannel sc, HttpRequest request) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        HttpMethod method = request.getHttpMethod();
        HttpResponse response = new HttpResponse(method);
        byte[] data = SerializeUtils.serializeObject(response);
        if (data.length > BUFFER_SIZE) {
            throw new ServerException("response is too big");
        }
        buffer.put(data);
        buffer.flip();
        sc.write(buffer);

        return response;
    }

    private final static int BUFFER_SIZE = 1024;

    private final static ExecutorService service = Executors.newCachedThreadPool();
}
