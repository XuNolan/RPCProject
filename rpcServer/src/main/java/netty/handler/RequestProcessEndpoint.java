package netty.handler;

import cn.hutool.core.util.ObjectUtil;
import dto.ResData;
import dto.RpcRequest;
import dto.RpcResponse;
import enums.ExceptionEnum;
import exception.RpcException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import register.ServiceRegister;

import java.lang.reflect.Method;
import java.util.Optional;

public class RequestProcessEndpoint extends ChannelDuplexHandler {
    //private static final Logger log = LoggerFactory.getLogger(RequestProcessEndpoint.class);
    public RequestProcessEndpoint(){
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof RpcRequest)) {
            throw new RpcException(ExceptionEnum.RpcRequestMsgInvalid);
        }
        RpcRequest rpcRequest = (RpcRequest) msg;
        try {
            Class<?> clazz = ServiceRegister.getService(rpcRequest.getClassName());
            assert ObjectUtil.isNotNull(clazz) && clazz != null;
            Object object = clazz.newInstance();
            Method method = clazz.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamType());
            assert ObjectUtil.isNotNull(method);

            Object invokeResult = method.invoke(object, rpcRequest.getParamValue());

            ResData resData = new ResData(rpcRequest.getId(), Optional.of(invokeResult));
            RpcResponse rpcResponse = RpcResponse.getSuccessResponse(resData, "构造res完成。结果为" + method.getReturnType().cast(invokeResult));
            ctx.channel().writeAndFlush(rpcResponse);
        } catch (AssertionError e) {
            ResData resData = new ResData(rpcRequest.getId(), Optional.empty());
            RpcResponse rpcResponse = RpcResponse.getFailResponse("未找到注册的服务", resData);
            ctx.channel().writeAndFlush(rpcResponse);
            //throw new RpcException(ExceptionEnum.RpcServiceNotFound, e);
        }
    }
}
