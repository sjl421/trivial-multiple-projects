package com.xxx.domain;

/**
 * Created by dhy on 17-3-28.
 * HttpRequest 请求，以两个换行作为结束符
 */
public class HttpRequest {

    private HttpMethod httpMethod;

    private String requestURl;

    public String getRequestURl() {
        return requestURl;
    }

    public void setRequestURl(String requestURl) {
        this.requestURl = requestURl;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }
}
