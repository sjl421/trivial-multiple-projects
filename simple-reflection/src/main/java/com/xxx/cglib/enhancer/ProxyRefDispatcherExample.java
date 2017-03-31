package com.xxx.cglib.enhancer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.ProxyRefDispatcher;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class ProxyRefDispatcherExample {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ProxyRefDispatcherExample.class);
        enhancer.setCallback((ProxyRefDispatcher) proxy -> new ProxyRefDispatcherExample());
        ProxyRefDispatcherExample instance = (ProxyRefDispatcherExample) enhancer.create();
        instance.getName();
    }

    public void getName() {
        System.out.println("ProxyRefDispatcherExample#getName");
    }
}
