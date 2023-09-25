package netty.handler;

import dto.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import service.ResultMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResponseProcessHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof Response)){
            return;
        }
        Response response = (Response) msg;
        //检查map是否已有等待获取的res
        Map<String, CompletableFuture<Object>> map = ResultMap.getResultMap();
        if(!map.containsKey(response.getId()))
            throw new RuntimeException();
        CompletableFuture<Object> positionToPut = ResultMap.getResultMap().get(response.getId());
        positionToPut.complete(response.getResult());
    }
}
