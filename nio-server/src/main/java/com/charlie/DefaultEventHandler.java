package com.charlie;

import com.charlie.event.EventHandler;
import com.charlie.event.ServiceListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dhy on 17-3-28.
 * 单例模式的事件处理器
 */
public enum  DefaultEventHandler implements EventHandler {

    INSTANCE {
        private final List<ServiceListener> listeners = new LinkedList<>();

        public synchronized void addListener(ServiceListener listener) {
            listeners.add(listener);
        }

        @Override
        public void executeOnAccept() {
            listeners.forEach(ServiceListener::onAccept);
        }

        @Override
        public void executeOnAccepted() {
            listeners.forEach(ServiceListener::onAccepted);
        }

        @Override
        public void executeOnRead() {
            listeners.forEach(ServiceListener::onRead);
        }

        @Override
        public void executeOnWrite() {
            listeners.forEach(ServiceListener::onWrite);
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
}
