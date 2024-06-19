package com.cl.framework.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ip地址工具类
 *
 * @author xhz
 */
public class IpUtil {

    public static String getLocalIp() {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return localhost.getHostAddress();
    }

}
