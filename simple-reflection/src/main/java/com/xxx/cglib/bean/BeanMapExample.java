package com.xxx.cglib.bean;

import net.sf.cglib.beans.BeanMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class BeanMapExample {
    @Test
    public void testBeanGenerator() {
        SampleBean bean = new SampleBean();
        BeanMap map = BeanMap.create(bean);
        bean.setValue("Hello cglib");
        Assert.assertEquals("Hello cglib", map.get("value"));
    }
}
