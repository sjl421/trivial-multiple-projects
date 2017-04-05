package com.xxx.cglib.bean;

import net.sf.cglib.beans.ImmutableBean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class ImmutableBeanExample {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testImmutableBean() {
        SampleBean bean = new SampleBean();
        bean.setValue("Hello world!");
        SampleBean immutableBean = (SampleBean) ImmutableBean.create(bean);
        Assert.assertEquals("Hello world!", immutableBean.getValue());
        bean.setValue("Hello world, again!");
        Assert.assertEquals("Hello world, again!", immutableBean.getValue());
        exception.expect(IllegalStateException.class);
        immutableBean.setValue("Hello cglib!");
    }
}
