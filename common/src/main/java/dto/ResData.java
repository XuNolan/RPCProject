package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class ResData implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String id;
    private Object result;
}