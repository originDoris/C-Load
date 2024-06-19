package com.cl.framework.core.web.core;

import com.cl.framework.core.enums.ServerType;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xhz
 */
public abstract class AbstractFactor implements Factor{

    @Override
    public ConcurrentMap<ServerType, Class<?>> agents() {

        ConcurrentMap<ServerType, Class<?>> agentMap = internals();
        final ConcurrentMap<ServerType, Class<?>> agents = com.cl.framework.core.runtime.DataTableMotor.agents(ServerType.HTTP, defaults(), agentMap);
        final Set<ServerType> scanned = new HashSet<>(agents.keySet());
        final Set<ServerType> keeped = agentMap.keySet();
        scanned.removeAll(keeped);
        scanned.forEach(agents::remove);

        if (!agents.containsKey(ServerType.HTTP)) {
            agents.put(ServerType.HTTP, agentMap.get(ServerType.HTTP));
        }
        return agents;
    }

    public abstract Class<?>[] defaults();
    public abstract ConcurrentMap<ServerType, Class<?>> internals();

    private Logger logger(){
        return LoggerFactory.getLogger(this.getClass());
    }
}
