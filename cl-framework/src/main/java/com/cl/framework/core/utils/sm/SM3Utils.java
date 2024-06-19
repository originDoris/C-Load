package com.cl.framework.core.utils.sm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * SM3
 *
 * @author xhz
 */
public class SM3Utils {



    public static String genContent(String data){
       return SmUtil.sm3(data);
    }
}
