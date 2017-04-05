package com.xxx.ioc.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhy on 17-4-5.
 *
 */
public class BeanReader {

    public BeanDefinition create(Class<?> clazz, Map<String, Object> valueMap) {
        String beanName = clazz.getName();
        Map<String, BeanProperty> beanProperties = new HashMap<>();
        return create(clazz, new BeanDefinition(beanName, beanProperties), valueMap);
    }

    private BeanDefinition create(Class<?> clazz, BeanDefinition beanDefinition, Map<String, Object> valueMap) {
        if (clazz.equals(Object.class)) {
            return beanDefinition;
        }
        for (Field field : clazz.getDeclaredFields()) {
            Object fieldValue = valueMap.get(field.getName());
            beanDefinition.addBeanProperty(field.getName(), field.getType(), fieldValue);
        }

        return create(clazz.getSuperclass(), beanDefinition, valueMap);
    }
}
