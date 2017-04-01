package com.xxx.cglib.bean;

import net.sf.cglib.beans.BeanCopier;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class BeanCopierExample {
    @Test
    public void testBeanCopier() {
        BeanCopier copier = BeanCopier.create(SampleBean.class, AnotherSampleBean.class, false);
        SampleBean bean = new SampleBean();
        bean.setValue("Hello cglib!");
        AnotherSampleBean anotherSampleBean = new AnotherSampleBean();
        copier.copy(bean, anotherSampleBean, null);
        Assert.assertEquals("Hello cglib!", anotherSampleBean.getValue());
    }
}
