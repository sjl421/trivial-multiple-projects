# Java序列化的高级认识

[参考文章:Java序列化的高级认识](https://www.ibm.com/developerworks/cn/java/j-lo-serial)

## 文章结构

- 序列化ID
- 静态变量序列化
- 父类的序列化与transient关键字
- 对敏感字段进行加密
- 序列化存储规则

### 序列化后的结构

对于 `com.xxx.serialize.SerializableObj` 中的对象序列化后为:

```
��sr%com.xxx.serialize.SerializableObj�i�Zz�mLnametLjava/lang/String;xptdhy
```

可以观察到几个特点:
    1. 序列化会保存对象的包名;
    2. 序列化会保存对象的部分域以及这些域的值;
    3. 序列化不会保存对象的静态域和静态方法;

### 序列化ID问题

- **情景**: 两个客户端A和B视图通过网络传递对象数据,A端将对象C序列化为二进制数据再传给B,B反序列化得到C;
- **问题**: C对象的全路径为 `com.xxx.serialize`,在A端和B端代码完全一致,也都是先了Serializable接口,序列化时总是提示不成功;
- **解决**: 虚拟机是否允许反序列化,不仅取决于类路径和功能代码是否一致,一个非常重要的功能是两个类的序列化ID是否一致;

序列化ID可以使用两种不同的生成策略:
    - 使用固定的1L,这样可以确保代码一致时反序列化一定成功;
    - 随机生成一个不重复的long类型数据,这样可以通过改变序列化ID来限制某些用户的使用;
   
#### 特性使用案例

![案例结构程序](http://hangyudu.oss-cn-shanghai.aliyuncs.com/sundries/serializable.gif)

Client 端通过 Facade Object 才可以与业务逻辑对象进行交互.而客户端的Facade Object不能由Client生成,而是需要Server端的生成,然后序列化后通过网络将二进制对象数据传给Client,Client负责反序列化得到Facade对象.该模式可以使得Client端程序的使用需要服务端的许可.该模式可以使得 Client 端程序的使用需要服务器端的许可，同时 Client 端和服务器端的 Façade Object 类需要保持一致。当服务器端想要进行版本更新时，只要将服务器端的 Façade Object 类的序列化 ID 再次生成，当 Client 端反序列化 Façade Object 就会失败，也就是强制 Client 端从服务器端获取最新程序。

#### 静态变量序列化

```java
 public class Test implements Serializable {

	private static final long serialVersionUID = 1L;

	public static int staticVar = 5;

	public static void main(String[] args) {
		try {
			//初始时staticVar为5
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream("result.obj"));
			out.writeObject(new Test());
			out.close();

			//序列化后修改为10
			Test.staticVar = 10;

			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
					"result.obj"));
			Test t = (Test) oin.readObject();
			oin.close();
			
			//再读取，通过t.staticVar打印新的值
			System.out.println(t.staticVar);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
```

最后我们得到的结果是 10:虽然序列化对象时 `staticVar` 为5,但是 `序列化并不保存静态变量`, **序列化保存的是对象的状态,而静态变量属于类的状态**!

### 父类的序列化与Transient关键字

- **情境**: 一个子类实现了 Serializable 接口,它的父类都没有实现该接口,序列化该子类对象,然后反序列化后输出父类定义的某变量的数值,该变量数值与序列化时的数值不同;
- **解决**: **要想将父类对象也序列化,就需要让父类也实现Serializable接口**.如果父类不实现的话,就**需要有默认的无参构造器**.在父类没有实现Serializable接口时,虚拟机不会序列化父类对象的,而一个java对象的构造必须有父对象,才有子对象,反序列化也不例外,所以反序列化时,为了构造父对象,只能调用父类的无参构造器作为默认的父对象.因此当我们取父对象的变量值时,它的值是调用父类无参构造函数后的值.如果我们考虑到这种序列化的情况,在父类无参构造函数中对变量进行初始化的.否则,父类变量值都是默认生命的值.
- Transient 关键字的作用是控制变量的序列化，在变量声明前加上该关键字，可以阻止该变量被序列化到文件中，在被反序列化后，transient 变量的值被设为初始值，如 int 型的是 0，对象型的是 null。

#### 特性使用案例

```
+---------------+
|               |
|               |
|  Serializable |
|               |
|               |
+---------------+
        ^
        |
        |
        |
+---------------+         +---------------+
|     Child     |         |    parent     |
|---------------|         |---------------|
|attr4          | ------> |  attr1        |
|transient attr5|         |  attr2        |
|               |         |  attr3        |
+---------------+         +---------------+
```

在上面这个图中,只有attr4会被序列化,而放在父类相对于使用`transient`的好处是,当有另外一个child时,放在父类中的类仍然不会被序列化;

### 对敏感字段加密

- **情境**: 服务器端给客户端发送序列化对象数据，对象中有一些数据是敏感的，比如密码字符串等，希望对该密码字段在序列化时，进行加密，而客户端如果拥有解密的密钥，只有在客户端进行反序列化时，才可以对密码进行读取，这样可以一定程度保证序列化对象的数据安全。
- **解决**: 在序列化过程中，虚拟机会试图调用对象类里的 `writeObject` 和 `readObject` 方法，进行用户自定义的序列化和反序列化，如果没有这样的方法，则默认调用是 ObjectOutputStream 的 defaultWriteObject 方法以及 ObjectInputStream 的 defaultReadObject 方法。用户自定义的 writeObject 和 readObject 方法可以允许用户控制序列化的过程，比如可以在序列化的过程中动态改变序列化的数值。基于这个原理，可以在实际应用中得到使用，用于敏感字段的加密工作，清单 3 展示了这个过程。

```java
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
```

### 序列化存储规则

```java
/**
 * Created by dhy on 17-3-27.
 * 多次声明和读取的测试类
 */
public class DoubleSerializableObj implements Serializable {

    private static final long serialVersionUID = -848199938308015433L;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("result.obj"));
        DoubleSerializableObj test = new DoubleSerializableObj();
        //试图将对象两次写入文件
        out.writeObject(test);
        out.flush();
        System.out.println(new File("result.obj").length());
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
    }
}
```

上面的程序,输出结果为:

```
64
69
true
```

虽然我们写入了两个对象,但是我们的对象只增加了5个字节;

Java 序列化机制为了节省磁盘空间，具有特定的存储规则， **当写入文件的为同一对象时，并不会再将对象的内容进行存储，而只是再次存储一份引用，** 上面增加的 5 字节的存储空间就是新增引用和一些控制信息的空间。反序列化时，恢复引用关系，使得清单 3 中的 t1 和 t2 指向唯一的对象，二者相等，输出 true。该存储规则极大的节省了存储空间。

```java
public class DoubleSerializableObj implements Serializable {

    private static final long serialVersionUID = -848199938308015433L;

    private String name = "serializable";

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
```

上面的程序仍然只是增加了5个字节,并且t1和t2得到的name都是 `serializable`.
