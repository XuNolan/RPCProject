package github.xunolan.rpcproject.exception;

import github.xunolan.rpcproject.enums.ExceptionEnum;

public class RpcException extends RuntimeException {
    private final int errorCode;
    private final String errorMsg;
    public RpcException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
    public RpcException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getDesc());
        this.errorCode = exceptionEnum.getCode();
        this.errorMsg = exceptionEnum.getDesc();
    }
    public RpcException(ExceptionEnum exceptionEnum, Throwable throwable){
        super(exceptionEnum.getDesc(), throwable);
        this.errorCode = exceptionEnum.getCode();
        this.errorMsg = exceptionEnum.getDesc();
    }

    public int getErrorCode() {
        return this.errorCode;
    }
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
