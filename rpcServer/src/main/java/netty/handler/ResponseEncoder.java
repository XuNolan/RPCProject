package netty.handler;

import cn.hutool.core.util.ObjectUtil;
import dto.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseEncoder extends MessageToByteEncoder<Response> {
    private final static Logger log = LoggerFactory.getLogger(ResponseEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Response response, ByteBuf byteBuf) throws Exception {
        log.info("sever发送响应");
        byte[] buffer = ObjectUtil.serialize(response);
        int length = buffer.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(buffer);
    }
}
