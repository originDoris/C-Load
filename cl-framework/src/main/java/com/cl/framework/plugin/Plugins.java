package com.cl.framework.plugin;

import com.cl.framework.plugin.annotation.*;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 默认的插件
 * @author xhz
 */
public interface Plugins {

    String INJECT = "inject";

    String SERVER = "server";

    String ERROR = "error";

    String RESOLVER = "resolver";

    String[] DATA = new String[]{
            INJECT, ERROR, SERVER,
            RESOLVER
    };
    ConcurrentMap<Class<? extends Annotation>, String> INFIX_MAP =
            new ConcurrentHashMap<Class<? extends Annotation>, String>() {
                {
                    put(Mongo.class, Infix.MONGO);
                    put(MySql.class, Infix.MYSQL);
                    put(Jooq.class, Infix.JOOQ);
                    put(Rpc.class, Infix.RPC);
                    put(Redis.class, Infix.REDIS);
                }
            };
    Set<Class<? extends Annotation>> INJECT_ANNOTATIONS = new HashSet<Class<? extends Annotation>>() {
        {
            addAll(INFIX_MAP.keySet());
            add(Inject.class);
        }
    };


    interface Infix {

        String MONGO = "mongo";

        String MYSQL = "mysql";

        String REDIS = "redis";
        String JOOQ = "jooq";
        String RPC = "rpc";

        String LOGGER = "logger";

        Set<String> STANDAND = new HashSet<String>() {
            {
                add(MONGO);
                add(MYSQL);
                add(REDIS);

                add(RPC);
                add(JOOQ);
                add(LOGGER);
            }
        };
    }
}
