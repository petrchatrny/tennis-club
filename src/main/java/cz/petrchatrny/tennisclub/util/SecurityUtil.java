package cz.petrchatrny.tennisclub.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Utility class for generating passwords, salts and hashed.
 */
public class SecurityUtil {
    /**
     * creates hash from input text by using SHA-256 algorithm
     *
     * @param text to be hashed
     * @param salt to strengthen the hash
     * @return hashed text
     */
    public static String hash(String text, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((text + salt).getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * generates random password using ascii characters
     *
     * @param length output length of password
     * @return random password
     */
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

    /**
     * generates random salt to strengthen the hash
     *
     * @param length of salt
     * @return generated salt
     */
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
