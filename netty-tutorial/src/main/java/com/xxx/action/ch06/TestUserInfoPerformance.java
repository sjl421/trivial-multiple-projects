package com.xxx.action.ch06;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by dhy on 2017/5/5.
 * 测试JDK序列化和其他序列化的性能
 */
public class TestUserInfoPerformance {
    public static void main(String[] args) throws IOException {
        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("Welcome to netty");
        int loop = 10000000;
        ByteArrayOutputStream bos;
        ObjectOutputStream os;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.flush();
            os.close();
            byte[] b = bos.toByteArray();
            bos.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("The jdk serializable cost time is : " + (endTime - startTime) + " ms");
        System.out.println("----------------------------------------");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte[] b = info.codec(buffer);
        }
        endTime = System.currentTimeMillis();
        System.out.println("The byte array serializable cost time is : " + (endTime - startTime) + " ms");
    }
}
