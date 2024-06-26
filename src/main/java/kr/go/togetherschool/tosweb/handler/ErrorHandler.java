package kr.go.togetherschool.tosweb.handler;

import kr.go.togetherschool.tosweb.common.BaseErrorCode;
import kr.go.togetherschool.tosweb.common.GeneralException;

public class ErrorHandler extends GeneralException {

    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}