package servlets.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dev.servlet.utils.PasswordUtils;

class PasswordTest {

    @org.junit.jupiter.api.Test
    void encryptDecrypt() {
        String strToEncrypt = "Hello World";
        String encrypted = PasswordUtils.encrypt(strToEncrypt);
        String decrypted = PasswordUtils.decrypt(encrypted);
        assertEquals(strToEncrypt, decrypted);
    }
}