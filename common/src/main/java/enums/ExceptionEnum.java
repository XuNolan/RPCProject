package enums;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum ExceptionEnum {
    NettyChannelClose(0,"信道未活动或已断开"),
    NettyMsgSendFail(1, "信道发送消息失败"),
    RpcProcessWaitFail(2, "RPC客户端在等待提供者响应的过程中中断或出现异常"),
    RpcResponseMsgInvalid(3, "收到无法解析的响应报文"),
    RpcRequestMsgInvalid(4, "收到无法解析的请求报文"),
    RpcServiceNotFound(5, "找不到对应的服务"),
    RpcServerInitFail(6,"Netty服务端初始化异常"),
    RpcClientInitFail(7,"Netty客户端初始化异常"),

    RpcSerializeFail(8, "序列化失败"),
    RpcDeserializeFail(8, "反序列化失败"),
            ;

    private final int code;
    private final String desc;

    public int getCode(){
        return this.code;
    }
    public String getDesc(){
        return this.desc;
    }
}
