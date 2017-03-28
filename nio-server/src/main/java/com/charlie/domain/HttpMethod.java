package com.charlie.domain;

/**
 * Created by dhy on 17-3-28.
 *
 */
public enum HttpMethod {
    GET(1),
    POST(2);

    HttpMethod(int index) {
        this.index = index;
    }

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
