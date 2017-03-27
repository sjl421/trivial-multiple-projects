package serialize;

import java.io.*;

/**
 * Created by dhy on 17-3-27.
 * 一个可以被序列化的对象
 */
public class SerializableObj implements Serializable {

    private static final long serialVersionUID = -1;

    private String name;

    private static int staticVar = 5;

    private final static String PATH = "/home/dhy/obj2";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws IOException {
        try (FileOutputStream out = new FileOutputStream(PATH);
            ObjectOutputStream oout = new ObjectOutputStream(out);
            FileInputStream in = new FileInputStream(PATH);
            ObjectInputStream oin = new ObjectInputStream(in)) {
            SerializableObj obj = new SerializableObj();
            obj.setName("dhy");
            oout.writeObject(obj);

            // 在对象序列化之后将值修改为100
            SerializableObj.staticVar = 100;
            SerializableObj sobj = (SerializableObj) oin.readObject();
            System.out.println(sobj.getName());
            System.out.println(SerializableObj.staticVar);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
