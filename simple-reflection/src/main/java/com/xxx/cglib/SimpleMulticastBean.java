package com.xxx.cglib;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class SimpleMulticastBean implements DelegatationProvider {

    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
