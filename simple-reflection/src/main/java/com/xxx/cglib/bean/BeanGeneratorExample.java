package com.xxx.cglib.bean;

import net.sf.cglib.beans.BeanGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class BeanGeneratorExample {
    @Test
    public void testBeanGenerator() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.addProperty("value", String.class);
        Object myBean = beanGenerator.create();

        Method setter = myBean.getClass().getMethod("setValue", String.class);
        setter.invoke(myBean, "Hello cglib!");
        Method getter = myBean.getClass().getMethod("getValue");
        Assert.assertEquals("Hello cglib!", getter.invoke(myBean));
    }
}
