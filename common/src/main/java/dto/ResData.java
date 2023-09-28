package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@AllArgsConstructor
@Data
public class ResData{
    private String id;
    private Optional<Object> result;
}