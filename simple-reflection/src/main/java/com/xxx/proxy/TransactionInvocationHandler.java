package com.xxx.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class TransactionInvocationHandler implements InvocationHandler {

    private ITransactionManager transactionManager;

    public TransactionInvocationHandler(ITransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("start transaction");
        Object invokeResult = method.invoke(transactionManager, args);
        System.out.println("commit");
        return invokeResult;
    }

}

