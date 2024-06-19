package com.cl.framework.core.runtime;


import com.cl.framework.core.codec.EnvelopCodec;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.enums.ServerType;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.eventbus.EventBus;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 项目启动工具类
 * @author xhz
 */
public final class DataTableMotor {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.cl.framework.core.runtime.DataTableMotor.class);



    public static void codec(final EventBus eventBus) {
        eventBus.getDelegate().registerDefaultCodec(Envelop.class, ReflectionUtils.singleton(EnvelopCodec.class));
    }

    public static ConcurrentMap<ServerType, Class<?>> agents(final ServerType category, final Class<?>[] defaultAgents, final ConcurrentMap<ServerType, Class<?>> internals) {
        final ConcurrentMap<ServerType, List<Class<?>>> agents = getMergedAgents(category, internals);
        final ConcurrentMap<ServerType, Boolean> defines = isAgentDefined(agents, defaultAgents);
        final ConcurrentMap<ServerType, Class<?>> ret = new ConcurrentHashMap<>();

        CubeFn.exec(agents, (type, list) -> {
            CubeFn.safeSemi(defines.containsKey(type) && defines.get(type),
                    () -> {
                        final Class<?> found = CubeFn.find(list, (item) -> internals.get(type) != item, LOGGER);
                        if (null != found) {
                            ret.put(type, found);
                        }
                    }, () -> {
                        final Class<?> found = CubeFn.find(list, (item) -> internals.get(type) == item, LOGGER);
                        if (null != found) {
                            LOGGER.info(MessageFormat.format(MessageConstant.AGENT_DEFINED, found, type));
                            ret.put(type, found);
                        }
                    }, LOGGER);
        });
        // 2.Filter
        return filterAgents(ret);
    }

    private static ConcurrentMap<ServerType, Class<?>> filterAgents(final ConcurrentMap<ServerType, Class<?>> agents) {
        // Check Socket Enabled
        if (DataTableConfig.getSockOpts().isEmpty()) {
            agents.remove(ServerType.SOCK);
        }
        return agents;
    }

    private static ConcurrentMap<ServerType, List<Class<?>>> getMergedAgents(final ServerType category, final ConcurrentMap<ServerType, Class<?>> internals) {
        final ConcurrentMap<ServerType, List<Class<?>>> agents = DataTableAnno.getAgents();
        if (agents.isEmpty()) {
            agents.put(category, new ArrayList<>(internals.values()));
        }
        return agents;
    }

    public static ConcurrentMap<ServerType, Boolean> isAgentDefined(final ConcurrentMap<ServerType, List<Class<?>>> agents, final Class<?>... exclude) {
        final Set<Class<?>> excludes = new HashSet<>(Arrays.asList(exclude));
        final ConcurrentMap<ServerType, Boolean> defined
                = new ConcurrentHashMap<>();
        for (final ServerType server : agents.keySet()) {
            final List<Class<?>> item = agents.get(server);
            final List<Class<?>> filtered = item.stream()
                            .filter(each -> !excludes.contains(each))
                            .collect(Collectors.toList());
            final int size = filtered.size();
            CubeFn.outError(LOGGER, 1 < size,
                    com.cl.framework.core.exception.DataTableException.class,
                    ErrorCodeEnum.AGENT_DUPLICATED_ERROR,
                    MessageFormat.format(ErrorInfoConstant.AGENT_DUPLICATED_ERROR, server, size,
                            filtered.stream()
                                    .map(Class::getName)
                                    .collect(Collectors.joining(","))));
            defined.put(server, 1 == size);
        }
        return defined;
    }
}
