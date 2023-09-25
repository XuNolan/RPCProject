package netty.handler;

import cn.hutool.core.util.ObjectUtil;
import dto.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseEncoder extends MessageToByteEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Response response, ByteBuf byteBuf) throws Exception {
        byte[] buffer = ObjectUtil.serialize(response);
        byteBuf.writeBytes(buffer);
    }
}
