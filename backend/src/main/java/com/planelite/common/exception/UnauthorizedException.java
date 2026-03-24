package com.planelite.common.exception;

/**
 * 未认证异常
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(401, message);
    }
}
