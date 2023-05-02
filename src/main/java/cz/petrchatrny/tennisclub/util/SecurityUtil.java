package cz.petrchatrny.tennisclub.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class SecurityUtil {
    public static String hash(String text, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((text + salt).getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateRandomPassword(int length) {
        Random random = new Random();
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < length; i++) {
            // random char from ascii 33 (!) to ascii 126 (~)
            char randomChar = (char) (random.nextInt(94) + '!');
            value.append(randomChar);
        }

        return value.toString();
    }

    public static String generateRandomSalt(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] saltBytes = new byte[length];
        secureRandom.nextBytes(saltBytes);
        return bytesToHex(saltBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
