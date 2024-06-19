package com.cl.framework.plugin.job;

import com.cl.framework.core.annotation.Plugin;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.job.JobClient;
import com.cl.framework.plugin.Infix;
import io.vertx.rxjava3.core.Vertx;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Plugin
@SuppressWarnings("unchecked")
public class JobInfix implements Infix {

    private static final String NAME = "DATACUBE_JOB_POOL";

    private static final ConcurrentMap<String, JobClient> CLIENTS
            = new ConcurrentHashMap<>();

    private static void initInternal(final String name) {
        CubeFn.pool(CLIENTS, name,
                () -> Infix.initTp("job",
                        (config) -> JobClient.createShared(config.getJsonObject("client")),
                        JobInfix.class));
    }

    public static void init(final Vertx vertx) {
        initInternal( NAME);
    }

    public static JobClient getClient() {
        return CLIENTS.get(NAME);
    }

    @Override
    public JobClient get() {
        return getClient();
    }
}
