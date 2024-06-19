package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.annotation.Agent;
import com.cl.framework.core.annotation.Anno;
import com.cl.framework.core.enums.ServerType;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 扫描Agent
 *
 * @author xhz
 */
public class AgentInquirer implements Inquirer<ConcurrentMap<ServerType, List<Class<?>>>>{

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentInquirer.class);
    @Override
    public ConcurrentMap<ServerType, List<Class<?>>> scan(Set<Class<?>> classes) {
        final Set<Class<?>> agents = classes.stream()
                .filter((item) -> item.isAnnotationPresent(Agent.class))
                .collect(Collectors.toSet());
        return new ConcurrentHashMap<>(agents.stream().collect(Collectors.groupingBy(aClass -> {
            return Anno.getAgentKey(aClass, LOGGER);
        })));
    }
}
