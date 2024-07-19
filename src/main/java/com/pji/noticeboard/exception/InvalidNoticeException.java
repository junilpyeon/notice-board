package com.pji.noticeboard.exception;

public class InvalidNoticeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final ErrorCode errorCode;

    public InvalidNoticeException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_PARAMETER;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
