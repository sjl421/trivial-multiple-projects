package com.xxx.proxy;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class WeakTm implements ITransactionManager {
    @Override
    public void transaction() {
        System.out.println("weak tm is running");
    }
}
