package enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResCodeEnum {
    Success(0, "通用成功代码"),
    SimpleFail(1, "通用失败代码"),

    ;
    private final int code;
    private final String codeDescribe;
    public int getCode(){
        return this.code;
    }
    public String getDesc(){
        return this.codeDescribe;
    }
}
