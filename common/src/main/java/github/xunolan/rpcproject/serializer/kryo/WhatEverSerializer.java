package github.xunolan.rpcproject.serializer.kryo;

import github.xunolan.rpcproject.serializer.Serializer;

public class WhatEverSerializer implements Serializer {
    static {
        System.out.println("init WhatEverSerializer");
    }
    @Override
    public byte[] serialize(Object object) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
