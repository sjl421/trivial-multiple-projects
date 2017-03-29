package com.charlie;

import com.charlie.domain.HttpRequest;
import com.charlie.domain.HttpResponse;
import com.charlie.event.EventHandler;
import com.charlie.event.ServiceListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dhy on 17-3-28.
 * 单例模式的事件处理器
 */
public enum  DefaultEventHandler implements EventHandler {

    INSTANCE;

    private final List<ServiceListener> listeners = new LinkedList<>();

    public synchronized void addListener(ServiceListener listener) {
        listeners.add(listener);
    }

    @Override
    public void executeOnAccept(String requestUrl) {
        listeners.forEach(listener -> listener.onAccept(requestUrl));
    }

    @Override
    public void executeOnAccepted(String requestUrl) {
        listeners.forEach(listener -> listener.onAccepted(requestUrl));
    }

    @Override
    public void executeOnRead(HttpRequest request) {
        listeners.forEach(listener -> listener.onRead(request));
    }

    @Override
    public void executeOnWrite(HttpRequest request, HttpResponse response) {
        listeners.forEach(listener -> listener.onWrite(request, response));
    }

    @Override
    public void executeOnError(String msg) {
        listeners.forEach(listener -> listener.onError(msg));
    }

    @Override
    public void executeOnClose() {
        listeners.forEach(ServiceListener::onClose);
    }
}
