package com.cl.framework.core.web.core;

import com.cl.framework.core.enums.ServerType;
import com.cl.framework.core.web.core.AbstractFactor;
import com.cl.framework.core.web.core.agent.DataTableHttpAgent;
import com.cl.framework.core.web.core.agent.DataTableSockAgent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * AbstractFactor
 *
 * @author xhz
 */
public class HttpFactor extends AbstractFactor {


    private static final Set<Class<?>> AGENT_SET = new HashSet<Class<?>>(){
        {
            add(DataTableHttpAgent.class);
            add(DataTableSockAgent.class);
        }
    };
    private static final ConcurrentMap<ServerType, Class<?>> INTERNALS = new ConcurrentHashMap<ServerType, Class<?>>() {
        {
            this.put(ServerType.HTTP, DataTableHttpAgent.class);
            this.put(ServerType.SOCK, DataTableSockAgent.class);
        }
    };


    @Override
    public Class<?>[] defaults() {
        return AGENT_SET.toArray(new Class<?>[]{});
    }

    @Override
    public ConcurrentMap<ServerType, Class<?>> internals() {
        return INTERNALS;
    }


}
