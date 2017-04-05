package com.xxx.ioc;

import com.xxx.ioc.bean.BeanDefinition;
import com.xxx.ioc.bean.BeanProperty;
import com.xxx.ioc.bean.BeanReader;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by dhy on 17-4-5.
 *
 */
public class BeanReaderTest {

    @Before
    public void setup() {
        reader = new BeanReader();
    }

    @Test
    public void testCreate() {
        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put("beanName", "xxx");
        valueMap.put("supBeanName", "yyy");
        BeanDefinition beanDefinition = reader.create(Bean.class, valueMap);
        assertEquals("com.xxx.ioc.BeanReaderTest$Bean", beanDefinition.getBeanName());
        Map<String, BeanProperty> properties = beanDefinition.getBeanProperties();
        assertEquals(2, properties.size());
        BeanProperty beanNameProperty = properties.get("beanName");
        assertEquals(String.class, beanNameProperty.getPropertyType());
        assertEquals("xxx", beanNameProperty.getPropertyValue());
        assertEquals("beanName", beanNameProperty.getPropertyName());


        BeanProperty supBeanNameProperty = properties.get("supBeanName");
        assertEquals(String.class, supBeanNameProperty.getPropertyType());
        assertEquals("yyy", supBeanNameProperty.getPropertyValue());
        assertEquals("supBeanName", supBeanNameProperty.getPropertyName());
    }

    private static class Bean extends SupBean {
        private String beanName;

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }
    }

    private static class SupBean {
        private String supBeanName;

        public String getSupBeanName() {
            return supBeanName;
        }

        public void setSupBeanName(String supBeanName) {
            this.supBeanName = supBeanName;
        }
    }

    private BeanReader reader;
}