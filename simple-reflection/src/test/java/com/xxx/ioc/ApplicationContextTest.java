package com.xxx.ioc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhy on 17-4-5.
 *
 */
public class ApplicationContextTest {
    @Before
    public void setup() {
        context = new ApplicationContext("com.xxx.ioc");
    }

    @Test
    public void testApplicationContext() {
        Person person = context.getBean("com.xxx.ioc.Person", Person.class);
        person.setAge(26);
        person.setName("xxx");
        Person samePerson = context.getBean("com.xxx.ioc.Person", Person.class);
        assertEquals("xxx", samePerson.getName());
        assertEquals(26, samePerson.getAge());
    }

    @Test
    public void testSubClass() {
        Male male = context.getBean("com.xxx.ioc.Male", Male.class);
        male.setName("xxx");
        male.setAge(26);
        male.setBust((short) 87);
        male.setHip((short) 87);
        male.setWaist((short) 87);
        Male sameMale = context.getBean("com.xxx.ioc.Male", Male.class);
        assertEquals("xxx", sameMale.getName());
        assertEquals(26, sameMale.getAge());
        assertEquals(87, sameMale.getBust());
        assertEquals(87, sameMale.getHip());
        assertEquals(87, sameMale.getWaist());
    }

    private ApplicationContext context;
}