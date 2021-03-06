package com.xxx.event;

import com.xxx.domain.HttpRequest;
import com.xxx.domain.HttpResponse;

/**
 * Created by dhy on 17-3-28.
 *
 */
public interface EventHandler {
    void executeOnAccept(String requestUrl);
    void executeOnAccepted(String requestUrl);
    void executeOnRead(HttpRequest request);
    void executeOnWrite(HttpRequest request, HttpResponse response);
    void executeOnError(String msg);
    void executeOnClose();
}
