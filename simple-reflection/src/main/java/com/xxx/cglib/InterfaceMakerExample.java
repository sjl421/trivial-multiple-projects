package com.xxx.cglib;

import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.InterfaceMaker;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class InterfaceMakerExample {
    @Test
    public void testInterfaceMaker() {
        Signature signature = new Signature("foo", Type.DOUBLE_TYPE, new Type[]{Type.INT_TYPE});
        InterfaceMaker maker = new InterfaceMaker();
        maker.add(signature, new Type[0]);
        Class iface = maker.create();
        Assert.assertEquals(1, iface.getMethods().length);
        Assert.assertEquals("foo", iface.getMethods()[0].getName());
        Assert.assertEquals(double.class, iface.getMethods()[0].getReturnType());
    }
}
