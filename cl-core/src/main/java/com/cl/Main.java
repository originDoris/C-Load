package com.cl;

import com.cl.framework.core.annotation.StartUp;
import com.cl.framework.core.vertx.VertxApplication;

/**
 * Main
 *
 * @author xhz
 */
@StartUp(scanBasePackages = "com.cl")
public class Main {
    public static void main(String[] args) {
        VertxApplication.run(Main.class);
    }
}