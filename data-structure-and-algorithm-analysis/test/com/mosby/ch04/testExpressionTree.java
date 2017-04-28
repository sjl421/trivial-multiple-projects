package com.mosby.ch04;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

public class testExpressionTree {
    private ExpressionTree expressionTree = null;
    
    @Before
    public void setUp(){
        expressionTree = new ExpressionTree();
    }
    
    @Test
    public void testGetNextNode() throws Exception{
        Method getNextNodeMtd = expressionTree.getClass().getDeclaredMethod("getNextNode", String.class, int.class);
        getNextNodeMtd.setAccessible(true);
        
        assertEquals("1", getNextNodeMtd.invoke("1+2", 0));
        assertEquals("1", getNextNodeMtd.invoke(expressionTree, "1+2", 0));
    }
}
