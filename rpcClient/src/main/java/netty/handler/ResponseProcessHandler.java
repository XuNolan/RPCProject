package netty.handler;

import dto.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ResultMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResponseProcessHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ResponseProcessHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof Response)){
            return;
        }
        Response response = (Response) msg;
        log.info("收到消息"+response.getResult().toString());
        //检查map是否已有等待获取的res
        Map<String, CompletableFuture<Object>> map = ResultMap.getResultMap();
        if(!map.containsKey(response.getId()))
            throw new RuntimeException();
        CompletableFuture<Object> positionToPut = ResultMap.getResultMap().get(response.getId());
        positionToPut.complete(response.getResult());
        ctx.channel().close();
    }
}
