package com.charlie.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dhy on 17-3-28.
 *
 */
public abstract class AbstractEventAdapater implements ServiceListener {

    public void onAccept() {
        LOGGER.debug("#accept");
    }

    public void onAccepted() {
        LOGGER.debug("#accepted");
    }

    public void onClose() {
        LOGGER.debug("#close");
    }

    public void onRead() {
        LOGGER.debug("#read");
    }

    public void onWrite() {
        LOGGER.debug("#write");
    }

    public void onError() {
        LOGGER.debug("#error");
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractEventAdapater.class);
}
