package com.xxx.proxy;

import java.lang.reflect.Proxy;



/**
 * Created by dhy on 17-3-31.
 *
 */
public class TransactionProxy {
    public static void main(String[] args) {
        ITransactionManager strongTmProxy = (ITransactionManager) Proxy.newProxyInstance(ITransactionManager.class.getClassLoader(),
                new Class[]{ITransactionManager.class},
                new TransactionInvocationHandler(new StrongTM()));
        strongTmProxy.transaction();

        ITransactionManager weakTmProxy = (ITransactionManager) Proxy.newProxyInstance(ITransactionManager.class.getClassLoader(),
                new Class[]{ITransactionManager.class},
                new TransactionInvocationHandler(new WeakTm()));
        weakTmProxy.transaction();
    }
}
