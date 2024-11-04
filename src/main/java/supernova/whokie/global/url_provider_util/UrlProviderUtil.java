package supernova.whokie.global.url_provider_util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class UrlProviderUtil {

    private static final String URL_SECRET_KEY = "dummy-key-123456"; // 16-byte key for AES

    private UrlProviderUtil() {
        // 인스턴스화 방지
    }

    public static String createUrl(Long groupId, LocalDateTime startDateTime,
        LocalDateTime endDateTime) {
        String data = groupId + "|" + startDateTime + "|" + endDateTime;
        try {
            return encrypt(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt URL", e);
        }
    }

    private static String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(URL_SECRET_KEY.getBytes(StandardCharsets.UTF_8),
            "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(encryptedBytes); // URL-safe encoding
    }

    private static String decrypt(String encryptedData) {
        try {
            String decryptedData = decryptData(encryptedData);
            return decryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to decrypt URL", e);
        }
    }

    private static String decryptData(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(URL_SECRET_KEY.getBytes(StandardCharsets.UTF_8),
            "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static UrlData parseUrlData(String encryptedUrl) {
        try {
            String decryptedData = decrypt(encryptedUrl);
            String[] parts = decryptedData.split("\\|");
            Long groupId = Long.parseLong(parts[0]);
            LocalDateTime startDateTime = LocalDateTime.parse(parts[1]);
            LocalDateTime endDateTime = LocalDateTime.parse(parts[2]);

            // 만료 시간 검사
            if (endDateTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("The invite code has expired.");
            }

            return UrlData.builder()
                .groupId(groupId)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse decrypted URL data", e);
        }
    }


}
