package com.charlie.event;

/**
 * Created by dhy on 17-3-28.
 *
 */
public interface ServiceListener {
    /**
     * 客户端尝试连接服务器时触发
     */
    void onAccept();

    /**
     * 客户端连接成功时触发
     */
    void onAccepted();

    /**
     * 客户端连接关闭时触发
     */
    void onClose();

    void onRead();

    void onWrite();

    void onError(String msg);
}
