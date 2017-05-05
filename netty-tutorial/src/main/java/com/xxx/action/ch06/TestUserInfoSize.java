package com.xxx.action.ch06;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by dhy on 2017/5/5.
 * 调用 {@link UserInfo#codec()} 和传统的 JDK 序列化后的码流大小进行对比
 */
public class TestUserInfoSize {
    public static void main(String[] args) throws IOException {
        UserInfo info = new UserInfo();
        info.buildUserID(100).buildUserName("Welcome to Netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(info);
        os.flush();
        os.close();
        byte[] b = bos.toByteArray();
        System.out.println("The jdk serializable length is : " + b.length);
        bos.close();
        System.out.println("----------------------------------------------");
        System.out.println("The byte array serializable length is : " + info.codec().length);
    }
}
