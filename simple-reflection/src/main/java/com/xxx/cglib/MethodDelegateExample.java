package com.xxx.cglib;

import com.xxx.cglib.bean.SampleBean;
import net.sf.cglib.reflect.MethodDelegate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class MethodDelegateExample {
    @Test
    public void testMethodDelegate() {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello cglib!");
        BeanDelegate delegate = (BeanDelegate) MethodDelegate.create(bean, "getValue", BeanDelegate.class);
        Assert.assertEquals("Hello cglib!", delegate.getValueFromDelegate());
    }
}
