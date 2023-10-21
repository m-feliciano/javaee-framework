package servlets.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dev.servlet.utils.PasswordUtils;

class PasswordTest {

    @org.junit.jupiter.api.Test
    void encryptDecrypt() throws Exception {
        String encrypt = "Hello World";
        String encrypted = PasswordUtils.encrypt(encrypt);
        String decrypted = PasswordUtils.decrypt(encrypted);
        assertEquals(encrypt, decrypted);
    }
}