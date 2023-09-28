package dto;

import enums.ResCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private ResCodeEnum resCode;
    private String describe;
    private ResData resData;

    private RpcResponse(){}

    public static RpcResponse getSuccessResponse(ResData resData, String describe){
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.resCode = ResCodeEnum.Success;
        rpcResponse.describe = describe;
        rpcResponse.resData = resData;
        return rpcResponse;
    }
    public static RpcResponse getFailResponse(ResCodeEnum resCode, String describe, ResData resData){
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.resCode = resCode;
        rpcResponse.describe = describe;
        rpcResponse.resData = resData;
        return rpcResponse;
    }

    public static RpcResponse getFailResponse(String describe, ResData resData){
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.resCode = ResCodeEnum.SimpleFail;
        rpcResponse.describe = describe;
        rpcResponse.resData = resData;
        return rpcResponse;
    }

}

