package ecommerce.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class MessageDto implements Serializable {
    private String id;
    private String content;

}
