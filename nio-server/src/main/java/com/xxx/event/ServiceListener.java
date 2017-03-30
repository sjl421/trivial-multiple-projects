package com.xxx.event;

import com.xxx.domain.HttpRequest;
import com.xxx.domain.HttpResponse;

/**
 * Created by dhy on 17-3-28.
 *
 */
public interface ServiceListener {
    /**
     * 客户端尝试连接服务器时触发
     */
    void onAccept(String requestUrl);

    /**
     * 客户端连接成功时触发
     */
    void onAccepted(String requestUrl);

    /**
     * 客户端连接关闭时触发
     */
    void onClose();

    void onRead(HttpRequest request);

    void onWrite(HttpRequest request, HttpResponse response);

    void onError(String msg);
}
