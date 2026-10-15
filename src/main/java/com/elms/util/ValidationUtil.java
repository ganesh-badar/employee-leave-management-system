package com.elms.util;

import com.elms.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Enterprise validation and sanitization utility.
 * Prevents XSS, checks formats, and enforces business invariants.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]{3,50}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+0-9\\s-]{7,25}$"
    );

    private static final Pattern EMPLOYEE_CODE_PATTERN = Pattern.compile(
            "^[A-Z0-9-]{3,30}$"
    );

    private ValidationUtil() {
        // utility class
    }

    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " is required and cannot be blank.");
        }
    }

    public static void validateEmail(String email) {
        requireNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("email", "Invalid corporate email format: " + email);
        }
    }

    public static void validateUsername(String username) {
        requireNonEmpty(username, "Username");
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            throw new ValidationException("username", "Username must be 3-50 characters and contain only letters, numbers, dots, dashes, or underscores.");
        }
    }

    public static void validatePassword(String password) {
        requireNonEmpty(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("password", "Password must be at least 6 characters long.");
        }
    }

    public static void validateEmployeeCode(String employeeCode) {
        requireNonEmpty(employeeCode, "Employee Code");
        if (!EMPLOYEE_CODE_PATTERN.matcher(employeeCode.trim().toUpperCase()).matches()) {
            throw new ValidationException("employeeCode", "Employee code must contain alphanumeric characters and hyphens (e.g. EMP-1001).");
        }
    }

    public static void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new ValidationException("phone", "Invalid telephone contact format.");
            }
        }
    }

    public static double parsePositiveDouble(String value, String fieldName) {
        requireNonEmpty(value, fieldName);
        try {
            double parsed = Double.parseDouble(value.trim());
            if (parsed <= 0) {
                throw new ValidationException(fieldName, fieldName + " must be a positive number.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName, fieldName + " must be a valid numeric amount.");
        }
    }

    public static int parsePositiveInt(String value, String fieldName) {
        requireNonEmpty(value, fieldName);
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed <= 0) {
                throw new ValidationException(fieldName, fieldName + " must be a positive integer.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName, fieldName + " must be a valid whole number.");
        }
    }

    /**
     * Sanitizes inputs to prevent XSS injection in raw HTML rendering.
     */
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
