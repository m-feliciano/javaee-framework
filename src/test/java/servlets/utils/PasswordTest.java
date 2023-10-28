package servlets.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dev.servlet.utils.CryptoUtils;

class PasswordTest {

    @org.junit.jupiter.api.Test
    void encryptDecrypt() throws Exception {
        String encrypt = "Hello World";
        String encrypted = CryptoUtils.encrypt(encrypt);
        String decrypted = CryptoUtils.decrypt(encrypted);
        assertEquals(encrypt, decrypted);
    }
}