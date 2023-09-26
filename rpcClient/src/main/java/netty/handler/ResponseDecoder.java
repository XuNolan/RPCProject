package netty.handler;

import cn.hutool.core.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import netty.ClientInit;

import java.util.List;

public class ResponseDecoder extends ByteToMessageDecoder {
    private Logger log = LoggerFactory.getLogger(ResponseDecoder.class);
    private int INTLENGTH = 4;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        log.info("decode收到响应");
        if (byteBuf.readableBytes() >= INTLENGTH){
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
            list.add(ObjectUtil.deserialize(data));
        }
    }
}
