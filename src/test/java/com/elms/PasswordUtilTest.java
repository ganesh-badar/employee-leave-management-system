package com.elms;

import com.elms.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Should generate distinct BCrypt hashes for identical passwords due to salt")
    void testHashAndSalt() {
        String raw = "SecurePass@123";
        String hash1 = PasswordUtil.hashPassword(raw);
        String hash2 = PasswordUtil.hashPassword(raw);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2, "BCrypt must produce distinct salts per hash");

        assertTrue(PasswordUtil.checkPassword(raw, hash1));
        assertTrue(PasswordUtil.checkPassword(raw, hash2));
    }

    @Test
    @DisplayName("Should reject incorrect credentials")
    void testRejectWrongPassword() {
        String hash = PasswordUtil.hashPassword("CorrectPassword");
        assertFalse(PasswordUtil.checkPassword("WrongPassword", hash));
        assertFalse(PasswordUtil.checkPassword(null, hash));
        assertFalse(PasswordUtil.checkPassword("CorrectPassword", null));
    }
}
