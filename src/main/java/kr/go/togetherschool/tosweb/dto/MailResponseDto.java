package kr.go.togetherschool.tosweb.dto;

import lombok.Builder;
import lombok.Data;

public class MailResponseDto {

    @Data
    @Builder
    public static class CheckMailResponseDto {
        private boolean isSuccess;
    }
}

