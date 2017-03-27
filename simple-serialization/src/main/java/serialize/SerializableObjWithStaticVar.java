package serialize;

import java.io.*;

/**
 * Created by dhy on 17-3-27.
 * 通过用户自定义 writeObject和readObject方法控制序列化的过程;
 */
public class SerializableObjWithStaticVar implements Serializable {

    private static final long serialVersionUID = -820997939288618363L;

    private String password = "pass";

    private void writeObject(ObjectOutputStream out) {
        try {
            ObjectOutputStream.PutField putFields = out.putFields();
            System.out.println("原密码: " + password);
            password = "encryption";//模拟加密
            putFields.put("password", password);
            out.writeFields();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream in) {
        try {
            ObjectInputStream.GetField readFields = in.readFields();
            Object object = readFields.get("password", "");
            System.out.println("要解密的字符串: " + object.toString());
            password = "pass";
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void main(String[] args) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("result.obj"));
            out.writeObject(new SerializableObjWithStaticVar());
            out.close();

            ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
                    "result.obj"));
            SerializableObjWithStaticVar t = (SerializableObjWithStaticVar) oin.readObject();
            System.out.println("解密后的字符串:" + t.getPassword());
            oin.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
