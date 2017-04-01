package com.xxx.cglib;

import net.sf.cglib.util.StringSwitcher;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class StringSwitcherExample {
    @Test
    public void testStringSwitcher() {
        String[] strings = {"one", "two"};
        int[] values = {10, 20};
        StringSwitcher stringSwitcher = StringSwitcher.create(strings, values, true);
        Assert.assertEquals(10, stringSwitcher.intValue("one"));
        Assert.assertEquals(20, stringSwitcher.intValue("two"));
        Assert.assertEquals(-1, stringSwitcher.intValue("three"));
    }
}
