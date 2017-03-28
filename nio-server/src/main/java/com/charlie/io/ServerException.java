package com.charlie.io;

import java.io.IOException;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class ServerException extends IOException {
    public ServerException() {

    }

    public ServerException(String msg) {
        super(msg);
    }
}
