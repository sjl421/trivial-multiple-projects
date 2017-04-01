package com.xxx.cglib;

import com.xxx.cglib.bean.SampleBean;
import net.sf.cglib.reflect.ConstructorDelegate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class ConstructorDelegateExample {
    @Test
    public void testConstructorDelegate() {
        SampleBeanConstructorDelegate constructorDelegate =
                (SampleBeanConstructorDelegate) ConstructorDelegate.create(SampleBean.class, SampleBeanConstructorDelegate.class);
        SampleBean bean = (SampleBean) constructorDelegate.newInstance();
        Assert.assertTrue(SampleBean.class.isAssignableFrom(bean.getClass()));
        Assert.assertTrue(SampleBean.class.equals(bean.getClass()));
    }
}
