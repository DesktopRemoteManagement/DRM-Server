package com.DarkKeks.drm;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

public class Security {

    private static final Random rand = new SecureRandom();
    private static final int KEY_SIZE = 256;
    private static final int KEY_ITERATION_COUNT = 16384;
    private static final int SALT_SIZE = 8;

    public static byte[] getEncryptedMessage(Message message) throws GeneralSecurityException {
        String msg = message.toString();
        byte[] salt = new byte[SALT_SIZE];
        rand.nextBytes(salt);
        SecretKey key = getEncryptionKey(Config.SECRET_KEY.toCharArray(), salt);

        return concatenateByteArrays(salt, encrypt(key, msg));
    }

    public static String getDecryptedMessage(byte[] message) throws GeneralSecurityException {
        byte[] salt = Arrays.copyOfRange(message, 0, SALT_SIZE);
        byte[] iv = Arrays.copyOfRange(message, SALT_SIZE, 16 + SALT_SIZE);
        message = Arrays.copyOfRange(message, 16 + SALT_SIZE, message.length);
        SecretKey key = getEncryptionKey(Config.SECRET_KEY.toCharArray(), salt);

        return decrypt(key, iv, message);
    }

    private static SecretKey getEncryptionKey(char[] password, byte[] salt) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, KEY_ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);

        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private static byte[] encrypt(SecretKey key, String msg) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] ciphertext = cipher.doFinal(msg.getBytes());

        return concatenateByteArrays(iv, ciphertext);
    }

    private static String decrypt(SecretKey key, byte[] iv, byte[] msg) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return new String(cipher.doFinal(msg));
    }

    private static byte[] concatenateByteArrays(byte[] a, byte[] b) throws IllegalStateException {
        byte[] result;
        try{
            ByteArrayOutputStream str = new ByteArrayOutputStream();
            str.write(a);
            str.write(b);
            result = str.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Some weird shit happened at Security.concatenateByteArrays");
        }
        return result;
    }
}
