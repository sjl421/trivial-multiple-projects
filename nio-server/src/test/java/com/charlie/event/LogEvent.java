package com.charlie.event;

import com.charlie.domain.HttpRequest;
import com.charlie.domain.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dhy on 17-3-29.
 * 在每次触发时间时通过日志输出参数
 */
public class LogEvent implements ServiceListener {
    @Override
    public void onAccept(String requestUrl) {
        LOGGER.debug(String.format("the remote client [%s] is trying to connect!", requestUrl));
    }

    @Override
    public void onAccepted(String requestUrl) {
        LOGGER.debug(String.format("the remote client [%s] connect successfully!", requestUrl));
    }

    @Override
    public void onClose() {
        LOGGER.debug("the remote client disconnect!");
    }

    @Override
    public void onRead(HttpRequest request) {
        LOGGER.debug(String.format("the remote client [%s] using [%s] method!", request.getRequestURl(), request.getHttpMethod()));
    }

    @Override
    public void onWrite(HttpRequest request, HttpResponse response) {

    }

    @Override
    public void onError(String msg) {

    }

    private final static Logger LOGGER = LoggerFactory.getLogger(LogEvent.class);
}
