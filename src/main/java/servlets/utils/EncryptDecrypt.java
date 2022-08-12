package servlets.utils;

import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptDecrypt {

    // TODO: Change this to your own salt/key and do not share it with anyone.
    private static final String SECRET_KEY = "$KJDN.LDNSpçÇCX.!@3MLkn*%151l$!@#$%^&*()_+";
    private static final String SALT = "LHDkdn@1220-90isphyuigkjn5976T8PYIHU";

    // This method use to encrypt to string
    public static String encrypt(String strToEncrypt) {
        try {

            // Create default byte array
            Cipher cipher = getCipher("AES/CBC/PKCS5Padding", Cipher.ENCRYPT_MODE);
            // Return encrypted string
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.err.println("Error while encrypting: " + e);
        }
        return null;
    }

    /**
     * This method use to decrypt to string
     *
     * @param strToDecrypt
     * @return
     */
    public static String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = getCipher("AES/CBC/PKCS5PADDING", Cipher.DECRYPT_MODE);
            // Return decrypted string
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.err.println("Error while decrypting: " + e);
        }
        return null;
    }

    /**
     * This method use to get cipher
     *
     * @param transformation
     * @param decryptMode
     * @return Cipher
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    @NotNull
    private static Cipher getCipher(String transformation, int decryptMode) throws NoSuchAlgorithmException,
                                                                                    InvalidKeySpecException,
                                                                                    NoSuchPaddingException,
                                                                                    InvalidKeyException,
                                                                                    InvalidAlgorithmParameterException {
        // Default byte array
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Create SecretKeyFactory Object
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        // Create KeySpec object and assign with constructor
        KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);

        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(decryptMode, new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES"), new IvParameterSpec(iv));
        return cipher;
    }
}
