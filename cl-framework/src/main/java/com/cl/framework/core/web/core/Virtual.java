package com.cl.framework.core.web.core;

/**
 * 虚拟的实现类 给加了注解但是没有实现类的方法使用，什么也不做
 *
 * @author xhz
 */
public class Virtual {

    private static Virtual INSTANCE = null;

    private Virtual() {
    }

    public static Virtual create() {
        if (null == INSTANCE) {
            synchronized (Virtual.class) {
                if (null == INSTANCE) {
                    INSTANCE = new Virtual();
                }
            }
        }
        return INSTANCE;
    }

    public static boolean is(final Object input) {
        boolean virtual = false;
        if (null != input && Virtual.class == input.getClass()) {
            virtual = true;
        }
        return virtual;
    }
}
