package com.cl.framework.core.utils.secure;

import io.vertx.core.impl.logging.Logger;

import java.text.MessageFormat;

class ScLog {

    private static void info(final Logger logger, final String pattern, final Object... args) {
        logger.info(MessageFormat.format(pattern, args));
    }

    private static void debug(final Logger logger, final String pattern, final Object... args) {
        logger.debug(MessageFormat.format(pattern, args));
    }

    private static void warn(final Logger logger, final String pattern, final Object... args) {
        logger.warn(MessageFormat.format(pattern, args));
    }

    static void infoAuth(final Logger logger, final String pattern, final Object... args) {
        info(logger, "Auth", pattern, args);
    }

    static void warnAuth(final Logger logger, final String pattern, final Object... args) {
        warn(logger, "Auth", pattern, args);
    }

    static void debugAuth(final Logger logger, final String pattern, final Object... args) {
        debug(logger, "Auth", pattern, args);
    }
}
