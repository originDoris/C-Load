package com.cl.framework.core.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 *
 * @author xhz
 */
public class Md5Utils {

    public static String md5(String input) {
        try {
            // 创建 MessageDigest 实例，指定算法为 MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将输入字符串转换为字节数组
            byte[] inputBytes = input.getBytes();

            // 计算字节数组的 MD5 哈希值
            byte[] hashBytes = md.digest(inputBytes);

            // 将字节数组转换为十六进制字符串
            BigInteger hashNumber = new BigInteger(1, hashBytes);
            StringBuilder hexString = new StringBuilder(hashNumber.toString(16));

            // 补齐前导零
            while (hexString.length() < 32) {
                hexString.insert(0, "0");
            }

            // 返回 MD5 哈希值
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
