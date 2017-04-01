package com.xxx.cglib.bean;

import net.sf.cglib.beans.BulkBean;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class BulkBeanExample {
    @Test
    public void testBulkBean() {
        BulkBean bulkBean = BulkBean.create(SampleBean.class,
                new String[]{"getValue"},
                new String[]{"setValue"},
                new Class[]{String.class});

        SampleBean bean = new SampleBean();
        bean.setValue("Hello world!");
        Assert.assertEquals(1, bulkBean.getPropertyValues(bean).length);
        Assert.assertEquals("Hello world!", bulkBean.getPropertyValues(bean)[0]);
        bulkBean.setPropertyValues(bean, new Object[]{"Hello cglib!"});
        Assert.assertEquals("Hello cglib!", bean.getValue());
    }
}
