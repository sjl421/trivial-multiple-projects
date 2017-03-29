package com.charlie;

import com.charlie.domain.HttpRequest;
import com.charlie.domain.HttpResponse;
import com.charlie.io.ClientException;
import com.charlie.util.SerializeUtils;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by dhy on 17-3-28.
 *
 */
public class Client {

    private SocketChannel sc;

    public Client(String host, int port) throws IOException {
        sc = SocketChannel.open();
        sc.configureBlocking(true);
        sc.connect(new InetSocketAddress(host, port));
        sc.finishConnect();
    }

    public void sendRequest(HttpRequest request) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        byte[] data = SerializeUtils.serializeObject(request);
        byte[] requestBytes = new byte[data.length + 2];
        System.arraycopy(data,0, requestBytes, 0, data.length);
        requestBytes[data.length] = '\n';
        requestBytes[data.length + 1] = '\n';
        // -2 是因为要留两个字节作为 HttpRequest 结束的分隔符
        if (data.length > BUFFER_SIZE-2) {
            throw new ClientException("HttpRequest is oversize, the max length is : " + BUFFER_SIZE);
        }
        buffer.put(requestBytes);
        buffer.flip();
        sc.write(buffer);
    }

    public HttpResponse acceptResponse() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(sc.socket().getInputStream());
        byte[] data = new byte[BUFFER_SIZE];
        int readBytes = dataInputStream.read(data);
        if (readBytes > 0) {
            return SerializeUtils.deserializeObject(data);
        }
        return null;
    }

    private final static int BUFFER_SIZE = 2048;
}
