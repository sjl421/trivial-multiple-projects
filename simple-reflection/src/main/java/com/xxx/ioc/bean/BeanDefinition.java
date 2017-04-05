package com.xxx.ioc.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dhy on 17-4-5.
 *
 */
public class BeanDefinition {
    private Object instance;
    private String beanName;
    private final Class<?> beanClass;
    private List<Class<?>> constructorArgs;
    private List<Object> constructorValues;
    private Map<String, BeanProperty> beanProperties;

    BeanDefinition(String beanName, Map<String, BeanProperty> beanProperties, Class<?> beanClass) {
        this(beanName, beanProperties, beanClass, null);
    }

    BeanDefinition(String beanName, Map<String, BeanProperty> beanProperties, Class<?> beanClass, List<BeanProperty> constructorInfo) {
        this.beanName = beanName;
        this.beanProperties = beanProperties;
        this.beanClass = beanClass;
        if (constructorInfo != null && !constructorInfo.isEmpty()) {
            constructorArgs = new ArrayList<>(constructorInfo.size());
            constructorValues = new ArrayList<>(constructorInfo.size());
            constructorInfo.forEach(c -> {
                constructorArgs.add(c.getPropertyType());
                constructorValues.add(c.getPropertyValue());
            });
        }
    }

    void addBeanProperty(String propertyName, Class<?> clazz, Object propertyValue) {
        beanProperties.put(propertyName, new BeanProperty(propertyName, clazz, propertyValue));
    }

    private Object createInstance() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (constructorArgs != null && !constructorArgs.isEmpty()) {
            Constructor<?> constructor = beanClass.getConstructor(constructorArgs.toArray(new Class<?>[constructorArgs.size()]));
            return constructor.newInstance(constructorValues.toArray(new Object[constructorValues.size()]));
        } else {
            return beanClass.newInstance();
        }
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

    public synchronized Object getInstance() {
        try {
            if (instance == null) {
                instance = createInstance();
            }
            return instance;
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
