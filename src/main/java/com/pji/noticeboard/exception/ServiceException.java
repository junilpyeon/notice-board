package com.pji.noticeboard.exception;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final ErrorCode errorCode;

    public ServiceException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
