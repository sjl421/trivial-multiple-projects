package com.xxx.cglib;

import com.xxx.cglib.bean.SampleBean;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class FastClassExample {
    @Test
    public void testFastClass() throws NoSuchMethodException, InvocationTargetException {
        FastClass fastClass = FastClass.create(SampleBean.class);
        FastMethod fastMethod = fastClass.getMethod(SampleBean.class.getMethod("getValue"));
        SampleBean myBean = new SampleBean();
        myBean.setValue("Hello cglib!");
        Assert.assertEquals("Hello cglib!", fastMethod.invoke(myBean, new Object[0]));
    }
}
