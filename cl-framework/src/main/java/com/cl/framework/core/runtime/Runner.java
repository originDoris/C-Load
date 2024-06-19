package com.cl.framework.core.runtime;

/**
 * 多线程运行
 * @author xhz
 */
public final class Runner {

    public static Thread run(final Runnable hooker,
                           final String name) {
        final Thread thread = new Thread(hooker);
        thread.setName(name + "-" + thread.getId());
        thread.start();
        return thread;
    }
}
