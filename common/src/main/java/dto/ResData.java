package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResData implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String id;
    private Object result;
}