package dev.menace.utils.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtils {

    private static SecretKeySpec key;

    public static void setKey(byte[] k) {
        key = new SecretKeySpec(k, "AES");
    }

    public static SecretKeySpec getKey() {
        return key;
    }

    public static byte[] generateKey() throws NoSuchAlgorithmException {
        // Generate a random AES key
        KeyGenerator  keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // You can choose 128, 192, or 256 bits key size
        SecretKey secretKey = keyGenerator.generateKey();

        // Convert the secret key to bytes for storage or transmission
        key = new SecretKeySpec(secretKey.getEncoded(), "AES");
        return secretKey.getEncoded();
    }

    public static byte[] encrypt(String input, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(byte[] encryptedBytes, SecretKeySpec key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
