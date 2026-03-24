package com.planelite.common.exception;

import lombok.Getter;

/**
 * 业务异常基类
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }
}
