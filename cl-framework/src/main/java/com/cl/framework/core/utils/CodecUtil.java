package com.cl.framework.core.utils;

import com.cl.framework.core.funcation.CubeFn;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * CodecUtil
 *
 * @author xhz
 */
public class CodecUtil {


    public static String sha512(final String input) {
        return sha(input, "SHA-512");
    }

    private static String sha(final String strText, final String strType) {
        return CubeFn.getDefault(null, () -> {
            final MessageDigest messageDigest;
            try {
                messageDigest = MessageDigest.getInstance(strType);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            messageDigest.update(strText.getBytes());
            final byte[] byteBuffer = messageDigest.digest();
            final StringBuilder strHexString = new StringBuilder();
            for (int i = 0; i < byteBuffer.length; i++) {
                final String hex = Integer.toHexString(0xff & byteBuffer[i]);
                if (hex.length() == 1) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            return strHexString.toString();
        }, strText, strType);
    }
}
