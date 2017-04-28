package com.mosby.ch08;

/**
 * Created by dhy on 2016/10/21.
 * Powered by dhy
 * 不相交集算法
 */
public class DisjSets {
    public DisjSets(int numElements) {
        s = new int[numElements];
        for (int i = 0; i < numElements; i++) {
            s[i] = -1;
        }
    }

    public void union(int root1, int root2) {
        s[root2] = root1;
    }

    public void unionBySize(int root1, int root2) {
        int size1 = -s[root1];
        int size2 = -s[root2];
        if (size1 >= size2) {
            s[root1]--;
            s[root2] = root1;
        } else {
            s[root2]--;
            s[root1] = root2;
        }
    }

    public void unionByDepth(int root1, int root2) {
        // 如果 root2 的深度大于 root1 的深度
        if (s[root2] < s[root1]) {
            s[root1] = root2;
        } else {
            // 如果 root1 的深度等于 root2，则深度加1
            if (s[root1] == s[root2]) {
                s[root1]--;
            }
            s[root2] = root1;
        }
    }

    public int find(int x) {
        if (s[x] < 0) {
            return x;
        } else {
            return find(s[x]);
        }
    }

    public int findWithPathCompress(int x) {
        if (s[x] < 0) {
            return x;
        } else {
            return s[x] = findWithPathCompress(s[x]);
        }
    }

    private int[] s;
}
