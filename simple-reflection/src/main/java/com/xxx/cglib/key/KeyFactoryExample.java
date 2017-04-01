package com.xxx.cglib.key;

import net.sf.cglib.core.KeyFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class KeyFactoryExample {
    @Test
    public void testKeyFactory() {
        SampleKeyFactory keyFactory = (SampleKeyFactory) KeyFactory.create(SampleKeyFactory.class);
        Object key0 = keyFactory.newInstance("foo", 42);
        Object key1 = keyFactory.newInstance("foo", 41);
        Object key2 = keyFactory.newInstance("foo", 42);

        Assert.assertEquals(false, key0.equals(key1));
        Assert.assertEquals(true, key0.equals(key2));
    }
}
