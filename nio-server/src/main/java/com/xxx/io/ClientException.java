package com.xxx.io;

import java.io.IOException;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class ClientException extends IOException {
    public ClientException() {

    }

    public ClientException(String msg) {
        super(msg);
    }
}
