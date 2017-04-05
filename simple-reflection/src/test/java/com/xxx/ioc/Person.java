package com.xxx.ioc;

import com.xxx.ioc.bean.MyBean;

/**
 * Created by dhy on 17-4-5.
 *
 */
@MyBean
public class Person {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
