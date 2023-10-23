package com.dev.servlet.utils;

import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.dev.servlet.domain.User;

public final class PasswordUtils {

	private static final String KEY = "lkuhJblhB562vhytit6767";
	private static final char[] hexArray = "0UIYW7YWIUKUSDUF".toCharArray();

	private PasswordUtils() {
	}

	public static byte[] decodebase64(String str) {
		return Base64.getDecoder().decode(str);
	}

	public static String encondeBase64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static byte[] hexToBytes(String str) {
		return HexFormat.of().parseHex(str);
	}

	public static String decrypt(String text) {
		try {
			SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decrypted = cipher.doFinal(hexToBytes(text));
			return new String(decrypted);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String encrypt(String text) {
		try {
			SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encrypted = cipher.doFinal(text.getBytes());
			return bytesToHex(encrypted);

		} catch (Exception e) {
			new RuntimeException(e);
		}
		return null;
	}

	/**
	 * Get a new token
	 *
	 * @param userDTO
	 * @return
	 */
	public static String generateToken(User user) {
		StringBuilder sb = new StringBuilder();
		long currentTimeInMilisecond = Instant.now().toEpochMilli();
		String token = sb.append(currentTimeInMilisecond)
				.append("-")
				.append(UUID.randomUUID().toString())
				.toString();

		CacheUtil.storeToken(token, new User(user.getId()));
		return token;
	}

	/**
	 * Verify if the token really exists
	 *
	 * @param parameter
	 * @return
	 */
	public static boolean isValidToken(String token) {
		if (token == null) {
			return false;
		}
		return CacheUtil.hasToken(token);
	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static boolean validate(String planText, String cipherText) {
		String encrypt = encrypt(planText);
		return encrypt != null && encrypt.equals(cipherText);
	}
}
