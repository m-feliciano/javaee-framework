package com.dev.servlet.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class PasswordUtils {

	private static final String SECRET_KEY = "$KJDN.LDNSpçÇCX.";
	private static final String SALT = "LHDkdn@1220-90isphyu";
	public static final String AES_CBC_PKCS_5_PADDING = "AES/CBC/PKCS5Padding";
	public static final String PBKDF_2_WITH_HMAC_SHA_256 = "PBKDF2WithHmacSHA256";
	public static final String AES = "AES";

	private PasswordUtils() {
	}

	/**
	 * Validate if the password is the same.
	 *
	 * @param encrypted
	 * @param password
	 * @return the boolean
	 */
	public static boolean validate(String encrypted, String password) {
		boolean valid = false;
		if (encrypted == null || password == null) {
			return valid;
		}

		return password.equals(decrypt(encrypted));
	}

	/**
	 * This method encrypt the string
	 *
	 * @param strToEncrypt
	 * @return
	 */
	public static String encrypt(String strToEncrypt) {
		try {

			// Create default byte array
			Cipher cipher = getCipher(AES_CBC_PKCS_5_PADDING, Cipher.ENCRYPT_MODE);
			// Return encrypted string
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * This method decrypt an encripted string
	 *
	 * @param strToDecrypt
	 * @return
	 */
	public static String decrypt(String strToDecrypt) {
		try {
			Cipher cipher = getCipher(AES_CBC_PKCS_5_PADDING, Cipher.DECRYPT_MODE);
			// Return decrypted string
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {

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
	private static Cipher getCipher(String transformation, int decryptMode) throws NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		// Default byte array
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		// Create SecretKeyFactory Object
		SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_256);

		// Create KeySpec object and assign with constructor
		KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);

		Cipher cipher = Cipher.getInstance(transformation);
		cipher.init(decryptMode, new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AES),
				new IvParameterSpec(iv));
		return cipher;
	}
}
