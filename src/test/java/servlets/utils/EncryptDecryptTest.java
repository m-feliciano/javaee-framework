package servlets.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncryptDecryptTest {

    @org.junit.jupiter.api.Test
    void encryptDecrypt() {
        String strToEncrypt = "Hello World";
        String encrypted = EncryptDecrypt.encrypt(strToEncrypt);
        String decrypted = EncryptDecrypt.decrypt(encrypted);
        assertEquals(strToEncrypt, decrypted);
    }
}