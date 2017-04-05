package com.xxx.ioc.bean;

import java.util.Map;

/**
 * Created by dhy on 17-4-5.
 *
 */
public class BeanDefinition {
    private String beanName;
    private Map<String, BeanProperty> beanProperties;

    BeanDefinition(String beanName, Map<String, BeanProperty> beanProperties) {
        this.beanName = beanName;
        this.beanProperties = beanProperties;
    }

    void addBeanProperty(String propertyName, Class<?> clazz, Object propertyValue) {
        beanProperties.put(propertyName, new BeanProperty(propertyName, clazz, propertyValue));
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Map<String, BeanProperty> getBeanProperties() {
        return beanProperties;
    }

    public void setBeanProperties(Map<String, BeanProperty> beanProperties) {
        this.beanProperties = beanProperties;
    }
}
