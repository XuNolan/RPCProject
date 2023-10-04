package github.xunolan.rpcproject.netty.handler.codec;

import github.xunolan.rpcproject.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import github.xunolan.rpcproject.serializer.Serializer;

public class ResponseEncoder extends MessageToByteEncoder<RpcResponse> {
    private final static Logger log = LoggerFactory.getLogger(ResponseEncoder.class);
    private final Serializer serializer;
    public ResponseEncoder(Serializer serializer){
        this.serializer = serializer;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) {
        log.info("sever发送响应");
        byte[] buffer = serializer.serialize(rpcResponse);
        int length = buffer.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(buffer);
    }
}
