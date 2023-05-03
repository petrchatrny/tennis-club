package cz.petrchatrny.tennisclub.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    @Test
    void hash() {
        String plainText = "some plain text";

        assertNotEquals(plainText, SecurityUtil.hash(plainText, ""));
        assertNotEquals(SecurityUtil.hash(plainText, ""), SecurityUtil.hash(plainText, "salt"));
    }

    @Test
    void generateRandomPassword() {
        String password = SecurityUtil.generateRandomPassword(20);
        assertNotNull(password);
        assertEquals(20, password.length());

        String secondPassword = SecurityUtil.generateRandomPassword(20);
        assertNotEquals(secondPassword, password);
    }

    @Test
    void generateRandomSalt() {
        String salt = SecurityUtil.generateRandomSalt(20);
        assertNotNull(salt);
        assertEquals(40, salt.length());

        String secondSalt = SecurityUtil.generateRandomSalt(20);
        assertNotEquals(secondSalt, salt);
    }
}