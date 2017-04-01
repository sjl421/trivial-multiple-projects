package com.xxx.cglib.bean;

import net.sf.cglib.beans.ImmutableBean;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class ImmutableBeanExample {
    @Test
    public void testImmutableBean() {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world!");
        SampleBean immutableBean = (SampleBean) ImmutableBean.create(bean);
        Assert.assertEquals("Hello world!", immutableBean.getValue());
        bean.setValue("Hello world, again!");
        Assert.assertEquals("Hello world, again!", immutableBean.getValue());
        immutableBean.setValue("Hello cglib!");
    }
}
