package com.dev.servlet.infrastructure.utils;

import com.dev.servlet.infrastructure.config.Properties;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CloudFrontCrypto {

    private static PrivateKey loadPrivateKey(Path path) throws Exception {
        String pem = Files.readString(path)
                // Remove PEM headers/footers and whitespace
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                // Remove PKCS#8 headers if present
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(pem);

        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    /**
     * Generate a custom policy for CloudFront signed cookies.
     * The policy allows access to private content for 1 hour.
     * Example of generated policy:
     * <pre>
     * {
     *   "Statement": [
     *     {
     *       "Resource": "eds23sample.cloudfront.net/private/*",
     *       "Condition": {
     *         "DateLessThan": { "AWS:EpochTime": 1700000000 }
     *       }
     *     }
     *   ]
     * }
     * </pre>
     * <p>
     * The domain is retrieved from application properties.
     *
     * @return the policy as a JSON string
     */
    private static String getPolicy() {
        String domain = Properties.get("cdn.domain");
        long expiresAt = Instant.now().getEpochSecond() + Duration.ofMinutes(15).toSeconds();

        return """
                {
                  "Statement": [
                    {
                      "Resource": "https://%s/private/*",
                      "Condition": {
                        "DateLessThan": { "AWS:EpochTime": %d }
                      }
                    }
                  ]
                }
                """.formatted(domain, expiresAt);
    }

    /**
     * Encode value in a CloudFront compatible way.
     * Replaces characters to make the string URL-safe for CloudFront.
     *
     * @param value the byte array to encode
     * @return the encoded string
     */
    private static String cfEncode(byte[] value) {
        return Base64.getEncoder()
                .encodeToString(value)
                .replace('+', '-')
                .replace('/', '~')
                .replace('=', '_');
    }

    /**
     * Generate signed cookies for CloudFront private content access.
     * Uses RSA private key to sign a custom policy.
     *
     * @return a map containing the signed cookies: "CloudFront-Policy", "CloudFront-Signature", and "CloudFront-Key-Pair-Id"
     * @throws Exception if any error occurs during key loading or signing
     */
    public static Map<String, String> generateCloudFrontSignedCookies() throws Exception {
        PrivateKey key = loadPrivateKey(
                Path.of(
                        Properties.getEnv("CLOUDFRONT_PRIVATE_KEY_PATH"))
        );
        String policy = getPolicy();

        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initSign(key);
        signer.update(policy.getBytes(StandardCharsets.UTF_8));
        byte[] signature = signer.sign();

        String encodedPolicy = cfEncode(policy.getBytes(StandardCharsets.UTF_8));
        String encodedSign = cfEncode(signature);

        return Map.of(
                "CloudFront-Policy", encodedPolicy,
                "CloudFront-Signature", encodedSign,
                "CloudFront-Key-Pair-Id", Properties.get("cdn.keyPairId")
        );
    }
}
