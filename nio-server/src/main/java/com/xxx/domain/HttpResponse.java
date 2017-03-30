package com.xxx.domain;

/**
 * Created by dhy on 17-3-28.
 *
 */
public class HttpResponse {

    public HttpResponse() {

    }

    public HttpResponse(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    // 返回用户请求时用的方法
    private HttpMethod httpMethod;

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }
}
