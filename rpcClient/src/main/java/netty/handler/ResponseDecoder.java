package netty.handler;

import cn.hutool.core.util.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ResponseDecoder extends ByteToMessageDecoder {
    private int INTLENGTH = 4;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
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
