package netty.handler;

import dto.RpcResponse;
import enums.ExceptionEnum;
import exception.RpcException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.ResultMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResponseProcessHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ResponseProcessHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if(!(msg instanceof RpcResponse)){
            throw new RpcException(ExceptionEnum.RpcResponseMsgInvalid);
        }
        RpcResponse rpcResponse = (RpcResponse) msg;
        String id = rpcResponse.getResData().getId();
        Map<String, CompletableFuture<RpcResponse>> map = ResultMap.getResultMap();
        if(!map.containsKey(id)){
            log.info("收到未请求的消息，id为"+ id);
            return;
        }
        //结果正确性在代理处理函数中进行。
        CompletableFuture<RpcResponse> positionToPut = ResultMap.getResultMap().get(id);
        positionToPut.complete(rpcResponse);
    }
}
