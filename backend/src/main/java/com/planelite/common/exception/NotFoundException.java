package com.planelite.common.exception;

/**
 * 资源不存在异常
 *
 * @author AI Coding Team
 * @since 2026-03-24
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(404, message);
    }
}
