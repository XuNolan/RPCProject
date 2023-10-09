package github.xunolan.rpcproject.netty.handler;

import cn.hutool.core.util.ObjectUtil;
import github.xunolan.rpcproject.dto.ResData;
import github.xunolan.rpcproject.dto.RpcRequest;
import github.xunolan.rpcproject.dto.RpcResponse;
import github.xunolan.rpcproject.enums.ExceptionEnum;
import github.xunolan.rpcproject.exception.RpcException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import github.xunolan.rpcproject.register.LocalServiceRecord;

import java.lang.reflect.Method;

public class RequestProcessEndpoint extends ChannelDuplexHandler {
//    private static final Logger log = LoggerFactory.getLogger(RequestProcessEndpoint.class);
    public RequestProcessEndpoint(){
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof RpcRequest)) {
            throw new RpcException(ExceptionEnum.RpcRequestMsgInvalid);
        }
        RpcRequest rpcRequest = (RpcRequest) msg;
        Class<?> clazz = LocalServiceRecord.getService(rpcRequest.getClassName());
        //空结果不应当报错。而是应当直接返回空结果。不是异常。
        if(ObjectUtil.isNotNull(clazz) && clazz != null) {
            Object object = LocalServiceRecord.getServiceInstance(clazz);
            Method method = clazz.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamType());
            if(ObjectUtil.isNotNull(method)){
                Object invokeResult = method.invoke(object, rpcRequest.getParamValue());
                ResData resData = new ResData(rpcRequest.getId(), invokeResult);
                RpcResponse rpcResponse = RpcResponse.getSuccessResponse(resData, "构造res完成。结果为" + method.getReturnType().cast(invokeResult));
                ctx.channel().writeAndFlush(rpcResponse);
                return;
            }
        }
        ResData resData = new ResData(rpcRequest.getId(), null);
        RpcResponse rpcResponse = RpcResponse.getFailResponse("未找到注册的服务", resData);
        ctx.channel().writeAndFlush(rpcResponse);
    }
}
