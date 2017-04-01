package com.xxx.cglib.mixin;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class Hello implements IHello {
    @Override
    public String hello() {
        return "hello";
    }
}
