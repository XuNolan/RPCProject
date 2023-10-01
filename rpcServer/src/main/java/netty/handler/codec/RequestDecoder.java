package netty.handler.codec;

import dto.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.Serializer;

import java.util.List;
public class RequestDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(RequestDecoder.class);
    private static final int INTLENGTH = 4;
    private final Serializer serializer;
    public RequestDecoder(Serializer serializer){
        this.serializer = serializer;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes()>=INTLENGTH){
            byteBuf.markReaderIndex();
            int dataLength = byteBuf.readInt();
            if(dataLength < 0 || byteBuf.readableBytes() < 0) {
                return;
            }
            if( byteBuf.readableBytes() < dataLength) {
                byteBuf.resetReaderIndex();
            }
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            log.info("收到msg，byte为",data);
            list.add(serializer.deserialize(data, RpcRequest.class));
        }

    }
}
