package com.planelite.common.exception;

/**
 * 无权限异常
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(403, message);
    }
}
