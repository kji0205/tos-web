package kr.go.togetherschool.tosweb.common;

import kr.go.togetherschool.tosweb.dto.ErrorReasonDTO;

public interface BaseErrorCode {

    ErrorReasonDTO getReason();

    ErrorReasonDTO getReasonHttpStatus();
}
