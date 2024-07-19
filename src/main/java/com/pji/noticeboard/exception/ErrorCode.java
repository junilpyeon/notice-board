package com.pji.noticeboard.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "Notice not found"),
    NOTICE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create notice"),
    NOTICE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update notice"),
    NOTICE_DELETION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete notice");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
