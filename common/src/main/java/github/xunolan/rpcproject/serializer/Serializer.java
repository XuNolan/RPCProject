package github.xunolan.rpcproject.serializer;

import github.xunolan.rpcproject.extension.SPI;

@SPI
public interface Serializer {
    byte[] serialize(Object object);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
