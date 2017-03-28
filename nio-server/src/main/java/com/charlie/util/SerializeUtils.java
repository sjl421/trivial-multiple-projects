package com.charlie.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by dhy on 17-3-22.
 *
 */
public class SerializeUtils {

    public static byte[] serializeObject(Object object) {
        if (object == null) {
            return null;
        }
        // kryo 非线程安全
        Kryo kryo = new Kryo();
        Output output = new Output(0, 40960);
        kryo.writeClassAndObject(output, object);
        byte[] result = output.toBytes();
        output.flush();
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        Kryo kryo = new Kryo();
        Input input = new Input(bytes);
        T t = (T) kryo.readClassAndObject(input);
        input.close();
        return t;
    }

    public static void main(String[] args) {
        Object o = new Object();
        serializeObject(o);
    }
}
