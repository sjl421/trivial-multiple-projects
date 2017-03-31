package com.xxx.cglib.enhancer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class NoOpExample {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(NoOpExample.class);
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return 0;
            }
        });
        NoOpExample instance = (NoOpExample) enhancer.create();
        instance.getName();
        System.out.println(instance.hashCode());
    }

    public void getName() {
        System.out.println("NoOpExample#getName");
    }
}
