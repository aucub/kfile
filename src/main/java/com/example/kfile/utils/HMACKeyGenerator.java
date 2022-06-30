package com.example.kfile.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HMACKeyGenerator {
    public static void main(String[] args) {
        try {
            // 选择HMAC算法为HmacSHA512
            String algorithm = "HmacSHA512";

            // 生成密钥
            SecretKey secretKey = generateSecretKey(algorithm);

            // 将密钥进行Base64编码输出
            String base64EncodedKey = encodeKey(secretKey);

            System.out.println("Generated HMAC512 Key: " + base64EncodedKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static SecretKey generateSecretKey(String algorithm) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(512); // 512 bits for HMAC512
        return keyGen.generateKey();
    }

    private static String encodeKey(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}

