package com.elms;

import com.elms.exception.ValidationException;
import com.elms.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    @DisplayName("Should accept valid corporate emails and reject invalid structures")
    void testEmailValidation() {
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("employee@internal.corp"));
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("john.doe@company.org"));

        assertThrows(ValidationException.class, () -> ValidationUtil.validateEmail("not-an-email"));
        assertThrows(ValidationException.class, () -> ValidationUtil.validateEmail("@domain.com"));
        assertThrows(ValidationException.class, () -> ValidationUtil.validateEmail(""));
    }

    @Test
    @DisplayName("Should sanitize HTML entities against Cross-Site Scripting (XSS)")
    void testXssSanitization() {
        String unsafe = "<script>alert('xss');</script>";
        String safe = ValidationUtil.sanitize(unsafe);
        assertFalse(safe.contains("<script>"));
        assertTrue(safe.contains("&lt;script&gt;"));
    }

    @Test
    @DisplayName("Should validate employee code formats")
    void testEmployeeCodeValidation() {
        assertDoesNotThrow(() -> ValidationUtil.validateEmployeeCode("EMP-1001"));
        assertDoesNotThrow(() -> ValidationUtil.validateEmployeeCode("ENG-99"));
        assertThrows(ValidationException.class, () -> ValidationUtil.validateEmployeeCode("!!bad!!"));
    }
}
