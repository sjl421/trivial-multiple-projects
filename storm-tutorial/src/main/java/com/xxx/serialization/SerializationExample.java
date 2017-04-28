package com.xxx.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.storm.Config;

/**
 * Created by dhy on 17-4-7.
 *
 */
public class SerializationExample {
    public static void main(String[] args) {
        Config config = new Config();
        config.registerSerialization(Integer.class);
        config.registerSerialization(String.class, CustomStringSerializer.class);
    }

    private static class CustomStringSerializer extends Serializer<String> {

        @Override
        public String read(Kryo kryo, Input input, Class type) {
            return "Hello world";
        }

        @Override
        public void write(Kryo kryo, Output output, String object) {
            // do nothing
        }
    }
}
