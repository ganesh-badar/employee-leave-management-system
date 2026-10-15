package com.elms.exception;

/**
 * Thrown when low-level JDBC or transactional operations fail.
 */
public class DatabaseException extends AppException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
