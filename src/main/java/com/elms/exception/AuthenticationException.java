package com.elms.exception;

/**
 * Thrown on invalid credentials, deactivated accounts, or unauthorized operations.
 */
public class AuthenticationException extends AppException {
    public AuthenticationException(String message) {
        super(message);
    }
}
