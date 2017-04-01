package com.xxx.cglib;

import net.sf.cglib.util.ParallelSorter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dhy on 17-4-1.
 *
 */
public class ParallelSorterExample {
    @Test
    public void testParallelSorter() {
        Integer[][] value = {
                {4, 3, 9, 0},
                {2, 1, 6, 0}
        };

        ParallelSorter.create(value).mergeSort(0);

        for (Integer[] row : value) {
            int former = -1;
            for (int val : row) {
                Assert.assertTrue(former < val);
                former = val;
            }
        }
    }
}
