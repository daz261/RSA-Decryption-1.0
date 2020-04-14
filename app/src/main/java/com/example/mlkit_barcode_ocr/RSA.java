package com.example.mlkit_barcode_ocr;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.util.Base64.decode;

public class RSA {
    static String private_key;
    //test if string is hex-encoded
    public static boolean TestHex(String value) {
        String re = "[0-9a-f]+";
        boolean result = value.matches(re);
        if (result) {
            return true;
        } else {
            return false;
        } }
    //converts hex to base64
    public static String hex(String encrypted) {
        //convert hex to string
        String hex = encrypted.replaceAll("^(00)+", "");
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        //convert string to base64
        byte[] encodedHexB64 = Base64.encode(bytes, Base64.DEFAULT);
        return new String(encodedHexB64).replace("\n", "");
    }
    //RSA decryption
    public static String decrypt_ocr(String cipherText) throws Exception {
        //put the private key string in PKCS8 format
        PrivateKey privateKey = getPrivateKey(private_key);
        String c = hex(cipherText);
        Log.i("base64", c);
        byte[] bytes = Base64.decode(c, Base64.DEFAULT);
        Cipher decriptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = decriptCipher.doFinal(bytes);
        return new String(decryptedBytes);
    }
    //get key
    public void get_key(String private_key) {
        this.private_key = private_key;
    }


    public static String decrypt(String cipherText) throws Exception {
        PrivateKey privateKey = getPrivateKey(private_key);
        byte[] bytes = Base64.decode(cipherText, Base64.DEFAULT);

        Cipher decriptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedBytes = decriptCipher.doFinal(bytes);
        return new String(decryptedBytes);

    }

    public static String encryptData(String text, String PUBLIC_KEY) {
        String encoded = "";
        byte[] encrypted;

        try {
            byte[] publicBytes = decode(PUBLIC_KEY, Base64.DEFAULT);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encrypted = cipher.doFinal(text.getBytes());
            encoded = Base64.encodeToString(encrypted, Base64.DEFAULT);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encoded;
    }


    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(base64PrivateKey.getBytes(), Base64.DEFAULT));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
    public String send_key(String private_key) {
        return private_key;
    }
}

