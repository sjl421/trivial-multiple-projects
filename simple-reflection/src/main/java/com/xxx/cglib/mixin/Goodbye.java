package com.xxx.cglib.mixin;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class Goodbye implements IGoodbye {
    @Override
    public String goodbye() {
        return "goodbye";
    }
}
