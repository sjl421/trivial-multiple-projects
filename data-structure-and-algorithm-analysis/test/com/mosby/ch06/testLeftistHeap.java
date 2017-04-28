package com.mosby.ch06;


import org.junit.Assert;
import org.junit.Test;

public class testLeftistHeap {
    @Test
    public void testMain(){
        int numItems = 100;
        LeftistHeap<Integer> h = new LeftistHeap<>();
        LeftistHeap<Integer> h1 = new LeftistHeap<>();
        int i;
        for( i = 37; i != 0; i = ( i + 37 ) % numItems ){
            if( i % 2 == 0 )
                h1.insert(i);
            else
                h.insert(i);
        }

        h.merge( h1 );

        for( i = 1; i < numItems; i++ ){
            if(h.deleteMin() != i)
                System.out.println( "Oops! " + i );
        }
        Assert.assertTrue(!h.isEmpty());
        h.makeEmpty();
        Assert.assertTrue(h.isEmpty());
    }
}
