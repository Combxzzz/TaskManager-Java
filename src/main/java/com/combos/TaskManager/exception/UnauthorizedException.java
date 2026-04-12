package com.combos.TaskManager.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    private final String action;
    private final String reason;

    public UnauthorizedException(String action, String reason) {
        super(String.format("Unauthorized to %s. %s", action, reason));
        this.action = action;
        this.reason = reason;
    }

    public UnauthorizedException(String message) {
        super(message);
        this.action = null;
        this.reason = null;
    }
}

