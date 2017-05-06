package com.xxx.action.ch06;

import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by dhy on 2017/5/5.
 * 通过实例查看Java序列化之后的大小
 */
@Message
public class UserInfo implements Serializable {
    private String userName;
    private int userID;

    public UserInfo buildUserName(String userName) {
        this.userName = userName;
        return this;
    }
    public UserInfo buildUserID(int userID) {
        this.userID = userID;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    // 使用基于 ByteBuffer 的通用二进制编码解码对UserInfo进行编码
    public byte[] codec() {
        return codec(null);
    }

    public byte[] codec(ByteBuffer buffer) {
        if (buffer == null) {
            buffer = ByteBuffer.allocate(1024);
        }
        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userID);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }
}
