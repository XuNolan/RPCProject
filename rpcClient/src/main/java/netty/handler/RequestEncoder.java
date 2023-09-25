package netty.handler;

import cn.hutool.core.util.ObjectUtil;
import dto.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestEncoder extends MessageToByteEncoder<Request> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Request request, ByteBuf byteBuf) throws Exception {
        byte[] buffer = ObjectUtil.serialize(request);
        int length = buffer.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(buffer);
        log.info("写入缓冲区",buffer);
    }
}
