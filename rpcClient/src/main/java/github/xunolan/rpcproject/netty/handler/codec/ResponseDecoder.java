package github.xunolan.rpcproject.netty.handler.codec;

import github.xunolan.rpcproject.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import github.xunolan.rpcproject.serializer.Serializer;

import java.util.List;


public class ResponseDecoder extends ByteToMessageDecoder {
    private final Logger log = LoggerFactory.getLogger(ResponseDecoder.class);
    private static final int INTLENGTH = 4;
    private final Serializer serializer;
    public ResponseDecoder(Serializer serializer){
        this.serializer = serializer;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        log.info("client收到响应");
        if (byteBuf.readableBytes() >= INTLENGTH){
            byteBuf.markReaderIndex();
            int dataLength = byteBuf.readInt();
            if(dataLength < 0 || byteBuf.readableBytes() < 0) {
                return;
            }
            if(byteBuf.readableBytes() < dataLength) {
                byteBuf.resetReaderIndex();
            }
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            list.add(serializer.deserialize(data, RpcResponse.class));
        }
    }
}
