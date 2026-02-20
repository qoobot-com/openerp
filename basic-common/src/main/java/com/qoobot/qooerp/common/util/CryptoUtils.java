package com.qoobot.qooerp.common.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密工具类
 */
public class CryptoUtils {

    /**
     * MD5加密
     */
    public static String md5(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        return SecureUtil.md5(input);
    }

    /**
     * SHA256加密
     */
    public static String sha256(String input) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        return SecureUtil.sha256(input);
    }

    /**
     * AES加密
     *
     * @param data 待加密数据
     * @param key  加密密钥（必须为16位）
     * @return 加密后的Base64字符串
     */
    public static String aesEncrypt(String data, String key) {
        if (StringUtils.isEmpty(data) || StringUtils.isEmpty(key)) {
            return null;
        }
        AES aes = new AES(key.getBytes(StandardCharsets.UTF_8));
        byte[] encrypted = aes.encrypt(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES解密
     *
     * @param encryptedData 加密数据
     * @param key           解密密钥（必须为16位）
     * @return 解密后的原始数据
     */
    public static String aesDecrypt(String encryptedData, String key) {
        if (StringUtils.isEmpty(encryptedData) || StringUtils.isEmpty(key)) {
            return null;
        }
        try {
            AES aes = new AES(key.getBytes(StandardCharsets.UTF_8));
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = aes.decrypt(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * 生成RSA密钥对
     *
     * @return RSA对象
     */
    public static RSA generateRSAKeyPair() {
        return SecureUtil.rsa();
    }

    /**
     * RSA公钥加密
     *
     * @param data       待加密数据
     * @param publicKey  公钥
     * @return 加密后的Base64字符串
     */
    public static String rsaEncrypt(String data, String publicKey) {
        if (StringUtils.isEmpty(data) || StringUtils.isEmpty(publicKey)) {
            return null;
        }
        try {
            RSA rsa = new RSA(null, publicKey);
            byte[] encrypted = rsa.encrypt(data.getBytes(StandardCharsets.UTF_8), KeyType.PublicKey);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }
    }

    /**
     * RSA私钥解密
     *
     * @param encryptedData 加密数据
     * @param privateKey    私钥
     * @return 解密后的原始数据
     */
    public static String rsaDecrypt(String encryptedData, String privateKey) {
        if (StringUtils.isEmpty(encryptedData) || StringUtils.isEmpty(privateKey)) {
            return null;
        }
        try {
            RSA rsa = new RSA(privateKey, null);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = rsa.decrypt(decoded, KeyType.PrivateKey);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    /**
     * Base64编码
     */
    public static String base64Encode(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     */
    public static String base64Decode(String encodedData) {
        if (StringUtils.isEmpty(encodedData)) {
            return null;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(encodedData);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Base64解码失败", e);
        }
    }

    /**
     * Base64编码（字节数组）
     */
    public static String base64Encode(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64解码（返回字节数组）
     */
    public static byte[] base64DecodeToBytes(String encodedData) {
        if (StringUtils.isEmpty(encodedData)) {
            return null;
        }
        return Base64.getDecoder().decode(encodedData);
    }
}
