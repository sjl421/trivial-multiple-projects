package com.charlie.event;

import com.charlie.domain.HttpRequest;
import com.charlie.domain.HttpResponse;

/**
 * Created by dhy on 17-3-28.
 *
 */
public interface EventHandler {
    void executeOnAccept();
    void executeOnAccepted();
    void executeOnRead();
    void executeOnWrite();
    void executeOnError(String msg);
    void executeOnClose();
}
