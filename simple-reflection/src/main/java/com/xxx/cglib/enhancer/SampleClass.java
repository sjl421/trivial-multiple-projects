package com.xxx.cglib.enhancer;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by dhy on 17-3-31.
 *
 */
public class SampleClass {
    public String test(String input) {
        return "Hello world!";
    }

    private String testPrivate() {
        return "private method!";
    }

    public final String finalTest() {
        return "final method!";
    }

    @Test
    public void testFixedValue() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallback((FixedValue) () -> "Hello cglib!");
        SampleClass proxy = (SampleClass) enhancer.create();
        assertEquals("Hello cglib!", proxy.test(null));
        assertEquals("private method!", proxy.testPrivate());
        assertEquals("final method!", proxy.finalTest());
    }
}
