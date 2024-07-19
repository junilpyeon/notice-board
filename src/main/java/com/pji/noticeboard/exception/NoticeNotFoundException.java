package com.pji.noticeboard.exception;

public class NoticeNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final ErrorCode errorCode;

    public NoticeNotFoundException(Long id) {
        super("Notice not found with id " + id);
        this.errorCode = ErrorCode.NOTICE_NOT_FOUND;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
