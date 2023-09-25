package netty.handler;

import dto.Request;
import dto.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import service.ServiceRegister;

import java.lang.reflect.Method;

@Slf4j
public class RequestProcessEndpoint extends ChannelInboundHandlerAdapter {
    private Channel channel;
    public RequestProcessEndpoint(Channel channel){
        this.channel = channel;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("收到msg");
        if(!(msg instanceof Request)) {
            log.info("收到非Request msg");
            return;
        }
        Request request;
        request = (Request) msg;
        //进行反射定位并且调用；
        //根据类限定名获取实例？不行。客户端并不知道包名。只知道调用哪个接口。
        Class clazz = ServiceRegister.getService(request.getClassName());
        Object object = clazz.getConstructor().newInstance();
        Method method = clazz.getMethod(request.getMethodName(), request.getParamType());
        Object invokeResult = method.invoke(object, request.getParamValue());
        Response response = new Response(request.getId(), invokeResult);
        log.info("构造res完成。结果为", String.class.cast(invokeResult));
        channel.writeAndFlush(response);
    }
}
