package com.xxx.cglib.mixin;

import net.sf.cglib.proxy.Mixin;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class MixinExample {
    @Test
    public void testMixin() {
        Mixin mixin = Mixin.create(new Class[]{IHello.class, IGoodbye.class, IMixin.class},
                new Object[]{new Hello(), new Goodbye()});

        IMixin mixinDelegate = (IMixin) mixin;
        Assert.assertEquals("hello", mixinDelegate.hello());
        Assert.assertEquals("goodbye", mixinDelegate.goodbye());
    }
}
