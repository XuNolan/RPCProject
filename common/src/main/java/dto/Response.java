package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Response implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String id;
    private Object result;
}
