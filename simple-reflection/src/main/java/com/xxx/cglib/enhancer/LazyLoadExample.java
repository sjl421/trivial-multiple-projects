package com.xxx.cglib.enhancer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import org.junit.Assert;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class LazyLoadExample {

    public LazyLoadExample(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(LazyLoadExample.class);
        enhancer.setCallback((LazyLoader) () -> {
            System.out.println("create a lazy loader instance");
            return new LazyLoadExample("xxx");
        });
        LazyLoadExample instance = (LazyLoadExample) enhancer.create(new Class[]{String.class}, new Object[]{"xxx"});
        LazyLoadExample instance1 = (LazyLoadExample) enhancer.create(new Class[]{String.class}, new Object[]{"xxx"});
        Assert.assertEquals("xxx", instance.getName());
        Assert.assertEquals("xxx", instance.getName());
        Assert.assertEquals("xxx", instance.getName());
        Assert.assertEquals("xxx", instance1.getName());
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
