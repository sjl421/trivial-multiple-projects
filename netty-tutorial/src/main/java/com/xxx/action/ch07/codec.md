MessagePack 编解码
=================


## MessagePack 介绍

- 编解码高效，性能高
- 序列化之后的码流小
- 支持跨语言

### API

```xml
<dependency>
    <groupId>org.msgpack</groupId>
    <artifactId>msgpack</artifactId>
    <version>${msgpack.version}</version>
</dependency>
```

```java
public class MessagePackExample {
    public static void main(String[] args) throws IOException {
        // Create serialize objects.
        ArrayList<String> src = new ArrayList<>();
        src.add("msgpack");
        src.add("kumofs");
        src.add("viver");
        MessagePack msgpack = new MessagePack();
        // Serialize
        byte[] raw = msgpack.write(src);
        // Deserialize directly using a template
        List<String> dst1 = msgpack.read(raw, Templates.tList(Templates.TString));
        System.out.println(dst1.get(0));
        System.out.println(dst1.get(1));
        System.out.println(dst1.get(2));
    }
}
```

