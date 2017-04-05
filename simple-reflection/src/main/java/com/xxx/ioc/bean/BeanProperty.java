package com.xxx.ioc.bean;

/**
 * Created by dhy on 17-4-5.
 *
 */
public class BeanProperty {

    private String propertyName;
    private Class<?> propertyType;
    private Object propertyValue;

    public BeanProperty(String propertyName, Class<?> propertyType, Object propertyValue) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Object propertyValue) {
        this.propertyValue = propertyValue;
    }
}
