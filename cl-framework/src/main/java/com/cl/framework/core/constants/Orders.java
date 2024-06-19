package com.cl.framework.core.constants;

/**
 * 用于管理标准请求流的默认顺序
 *
 * @author xhz
 */
public class Orders {
    public static final int MONITOR = 1_000_000;
    public static final  int CORS = 1_100_000;
    public static final  int COOKIE = 1_200_000;
    public static final  int BODY = 1_300_000;
    public static final  int CONTENT = 1_400_000;
    public static final  int SESSION = 1_600_000;
    public static final  int FILTER = 1_800_000;
    public static final int SECURE = 1_900_000;
    public static final int SIGN = 3_000_000;
    public static final int EVENT = 5_000_000;
    public static final int DYNAMIC = 6_000_000;
    public static final int MODULE = 10_000_000;
}
