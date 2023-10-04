package github.xunolan.rpcproject.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import github.xunolan.rpcproject.dto.RpcRequest;
import github.xunolan.rpcproject.dto.RpcResponse;
import github.xunolan.rpcproject.enums.ExceptionEnum;
import github.xunolan.rpcproject.exception.RpcException;
import github.xunolan.rpcproject.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class KryoSerializer implements Serializer {
    private static final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8){
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };

    @Override
    public byte[] serialize(Object object) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoPool.obtain();
            kryo.writeObject(output, object);
            kryoPool.free(kryo);
            return output.toBytes();
        } catch (Exception e) {
            throw new RpcException(ExceptionEnum.RpcSerializeFail, e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoPool.obtain();
            Object o = kryo.readObject(input, clazz);
            kryoPool.free(kryo);
            return clazz.cast(o);
        } catch (IOException e) {
            throw new RpcException(ExceptionEnum.RpcDeserializeFail, e);
        }
    }
}
