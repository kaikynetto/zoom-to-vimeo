package com.example.zoomtovimeo.utils;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMACUtil {

    public static String generateHMAC(String message, String secretKey) {
        String algorithm = "HmacSHA256";
        String hashForVerify = null;
        try {
            Mac hmacSha256 = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), algorithm);
            hmacSha256.init(secretKeySpec);
            byte[] hashBytes = hmacSha256.doFinal(message.getBytes());
            hashForVerify = bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return hashForVerify;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
