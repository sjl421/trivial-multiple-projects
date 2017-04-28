package com.mosby.ch04;

import com.mosby.common.structure.BinaryNode;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class testBinaryNode {
    private BinaryNode<String> node0;
    private BinaryNode<String> node1;
    private BinaryNode<String> node2;
    private BinaryNode<String> node3;
    
    @Before
    public void setup(){
        node0 = new BinaryNode<>("root");
        node1 = new BinaryNode<>("root");
        node2 = new BinaryNode<>("left");
        node3 = new BinaryNode<>("right");
    }
    
    @Test
    public void testEquals(){
        assertFalse(node0.equals(null));
        
        assertTrue(node0.equals(node1));
       
        node0.left = node2;
        assertTrue(!node0.equals(node1));
        
        node1.left = node2;
        assertTrue(node0.equals(node1));
        
        node0.left.right = node3;
        node1.right = node3;
        assertTrue(!node0.equals(node1));
    }
}
