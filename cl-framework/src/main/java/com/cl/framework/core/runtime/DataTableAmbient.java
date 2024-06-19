package com.cl.framework.core.runtime;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.options.visitor.ConfigVisitor;
import com.cl.framework.core.options.visitor.InjectVisitor;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Critical XHeader
 *
 * @author xhz
 */
public final class DataTableAmbient {

    private static final String KEY = "inject";

    private static final Logger LOGGER = LoggerFactory.getLogger(com.cl.framework.core.runtime.DataTableAmbient.class);

    private static final ConcurrentMap<String, Class<?>> INJECTIONS;

    private static final ConfigVisitor<JsonObject> INJECT_VISITOR = ReflectionUtils.singleton(InjectVisitor.class);

    static {
        INJECTIONS = new ConcurrentHashMap<>();

        final JsonObject injectOpt = new JsonObject();

        final JsonObject opt = INJECT_VISITOR.visit(KEY);
        if (opt != null) {
            injectOpt.mergeIn(opt);
        }

        for (Map.Entry<String, Object> entry : injectOpt) {
            String field = entry.getKey();
            String plugin = entry.getValue().toString();
            LOGGER.info(MessageFormat.format(MessageConstant.PLUGIN_LOAD, KEY, field, plugin));
            INJECTIONS.put(field, ReflectionUtils.clazz(plugin));
        }

    }

    private DataTableAmbient() {
    }

    public static Class<?> getPlugin(final String key) {
        return INJECTIONS.get(key);
    }

    public static ConcurrentMap<String, Class<?>> getInjections() {
        return INJECTIONS;
    }


    public static void init() {
    }
}
