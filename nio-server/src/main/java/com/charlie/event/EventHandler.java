package com.charlie.event;

import com.charlie.domain.HttpRequest;
import com.charlie.domain.HttpResponse;

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
