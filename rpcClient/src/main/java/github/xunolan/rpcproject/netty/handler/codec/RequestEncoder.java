package github.xunolan.rpcproject.netty.handler.codec;

import github.xunolan.rpcproject.dto.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import github.xunolan.rpcproject.serializer.Serializer;


public class RequestEncoder extends MessageToByteEncoder<RpcRequest> {
    private final Logger log = LoggerFactory.getLogger(RequestEncoder.class);
    private final Serializer serializer;
    public RequestEncoder(Serializer serializer){
        this.serializer = serializer;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) {
        byte[] buffer = serializer.serialize(rpcRequest);
        int length = buffer.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(buffer);
        log.info("写入缓冲区",buffer);
    }
}
