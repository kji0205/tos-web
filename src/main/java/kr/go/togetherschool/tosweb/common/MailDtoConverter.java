package kr.go.togetherschool.tosweb.common;


import kr.go.togetherschool.tosweb.dto.MailResponseDto;

public class MailDtoConverter {

    public static MailResponseDto.CheckMailResponseDto convertCheckMailResultToDto(boolean result) {
        return MailResponseDto.CheckMailResponseDto.builder()
                .isSuccess(result)
                .build();
    }
}
