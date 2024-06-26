package kr.go.togetherschool.tosweb.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ImageDto {
    private String accessUri;
    private String authenticateId;
    private String imgUrl;
}
