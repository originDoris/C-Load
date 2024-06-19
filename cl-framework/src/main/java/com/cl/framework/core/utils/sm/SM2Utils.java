package com.cl.framework.core.utils.sm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.security.*;

/**
 * SM2 加解密工具
 *
 * @author xhz
 */
public class SM2Utils {


    public static String encrypt(String body,String publicKey){
        ECPublicKeyParameters ecPublicKeyParameters ;
        //这里需要根据公钥的长度进行加工
        if (publicKey.length() == 130) {
            //这里需要去掉开始第一个字节 第一个字节表示标记
            publicKey = publicKey.substring(2);
            String xhex = publicKey.substring(0, 64);
            String yhex = publicKey.substring(64, 128);
            ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
        } else {
            PublicKey p = BCUtil.decodeECPoint(publicKey, SmUtil.SM2_CURVE_NAME);
            ecPublicKeyParameters = BCUtil.toParams(p);
        }
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);
        return sm2.encryptBase64(body, KeyType.PublicKey);
    }

    public static String decrypt(String data, boolean tab, String sm2PrivateKey) {
        if (tab) {
            data = "04" + data;
        }


        SM2 sm21 = SmUtil.sm2(sm2PrivateKey, null);
        return StrUtil.utf8Str(sm21.decryptStr(data, KeyType.PrivateKey));
    }
}
