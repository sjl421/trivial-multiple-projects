package com.xxx.cglib;

import net.sf.cglib.reflect.MulticastDelegate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class MulticastDelegateExample {
    @Test
    public void testMulticastDelegate() {
        MulticastDelegate multicastDelegate = MulticastDelegate.create(DelegatationProvider.class);
        SimpleMulticastBean first = new SimpleMulticastBean();
        SimpleMulticastBean second = new SimpleMulticastBean();
        multicastDelegate = multicastDelegate.add(first);
        multicastDelegate = multicastDelegate.add(second);

        DelegatationProvider provider = (DelegatationProvider) multicastDelegate;
        provider.setValue("Hello world!");

        Assert.assertEquals("Hello world!", first.getValue());
        Assert.assertEquals("Hello world!", second.getValue());
    }
}
