package serialize;

import java.io.*;
import java.util.Objects;

/**
 * Created by dhy on 17-3-27.
 * 多次声明和读取的测试类
 */
public class DoubleSerializableObj implements Serializable {

    private static final long serialVersionUID = -848199938308015433L;

    private String name = "serializable";

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DoubleSerializableObj)) {
            return false;
        }
        DoubleSerializableObj serializableObj = (DoubleSerializableObj) obj;
        return Objects.equals(this.name, serializableObj.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("result.obj"));
        DoubleSerializableObj test = new DoubleSerializableObj();
        //试图将对象两次写入文件
        out.writeObject(test);
        out.flush();
        System.out.println(new File("result.obj").length());
        test.setName("editor");
        out.writeObject(test);
        out.close();
        System.out.println(new File("result.obj").length());

        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
                "result.obj"));
        //从文件依次读出两个文件
        DoubleSerializableObj t1 = (DoubleSerializableObj) oin.readObject();
        DoubleSerializableObj t2 = (DoubleSerializableObj) oin.readObject();
        oin.close();

        //判断两个引用是否指向同一个对象
        System.out.println(t1 == t2);
        System.out.println(t1.getName());
        System.out.println(t2.getName());
    }
}
