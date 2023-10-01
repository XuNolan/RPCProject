package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String id;
    private String ClassName;
    private String methodName;
    private Class[] paramType;
    private Object[] paramValue;
}
