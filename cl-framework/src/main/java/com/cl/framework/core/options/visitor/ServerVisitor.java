package com.cl.framework.core.options.visitor;

import java.util.concurrent.ConcurrentMap;

/**
 * ServerVisitor
 *
 * @author xhz
 */
public interface ServerVisitor<T> extends ConfigVisitor<ConcurrentMap<Integer, T>> {

    String YKEY_TYPE = "type";

    String YKEY_CONFIG = "config";

    String YKEY_NAME = "name";

    String KEY = "server";
}
