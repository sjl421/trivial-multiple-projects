package com.charlie.domain;

import java.nio.channels.SocketChannel;

/**
 * Created by dhy on 17-3-28.
 *
 */
public class HttpResponse {

    public HttpResponse(SocketChannel sc) {
        this.sc = sc;
    }

    private final SocketChannel sc;
}
