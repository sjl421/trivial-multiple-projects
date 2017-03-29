package com.charlie.io;

import com.charlie.domain.HttpRequest;
import com.charlie.util.SerializeUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 * Created by dhy on 17-3-28.
 *
 */
public class Reader {

    public static HttpRequest processRequest(SocketChannel sc) {
        try {
            return service.submit(() -> read(sc)).get(100, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpRequest read(SocketChannel sc) throws IOException {
        ByteBuffer data = ByteBuffer.allocate(BUFFER_SIZE);
        while (sc.read(data) > 0) {
            // 当channel中有数据时一直读取
        }
        data.flip();
        int endpoint = endpointOfRequest(data);
        if (endpoint == -1) {
            throw new ServerException("查询HttpRequest失败");
        }
        byte[] request = new byte[endpoint];
        data.get(request, 0, endpoint);
        return SerializeUtils.deserializeObject(request);
    }

    private static int endpointOfRequest(ByteBuffer data) {
        int endpoint;
        boolean flag = false;
        for (endpoint = 0; endpoint < data.limit(); endpoint++) {
            if (data.get(endpoint) == NEWLINE) {
                if (flag) {
                    return endpoint-1;
                } else {
                    flag = true;
                }
            } else {
                flag = false;
            }
        }
        return -1;
    }

    private final static byte NEWLINE = '\n';

    private final static int BUFFER_SIZE = 2048;

    private final static ExecutorService service = Executors.newCachedThreadPool();
}
