package netty.handler.codec;

import cn.hutool.core.util.ObjectUtil;

import dto.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestEncoder extends MessageToByteEncoder<RpcRequest> {
    private final Logger log = LoggerFactory.getLogger(RequestEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) {
        byte[] buffer = ObjectUtil.serialize(rpcRequest);
        int length = buffer.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(buffer);
        log.info("写入缓冲区",buffer);
    }
}
