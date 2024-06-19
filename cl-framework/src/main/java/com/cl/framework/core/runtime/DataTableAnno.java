package com.cl.framework.core.runtime;

import com.cl.framework.core.enums.ServerType;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.agent.Hook;
import com.cl.framework.core.web.core.param.resolver.PathResolver;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.inquirer.*;
import com.cl.framework.core.web.core.worker.Mission;
import com.cl.framework.core.web.core.worker.Receipt;
import com.cl.framework.core.web.core.secure.Cliff;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 负责核心注解的扫描
 *
 * @author xhz
 */
@Slf4j
public class DataTableAnno {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.cl.framework.core.runtime.DataTableAnno.class);
    private final static Set<Class<?>> ENDPOINTS = new HashSet<>();

    private final static Set<Event> EVENTS = new HashSet<>();

    private final static Set<Hook> INITIAL = new HashSet<>();
    private final static Set<Hook> DESTROY = new HashSet<>();

    private final static ConcurrentMap<String, Set<Event>> FILTERS = new ConcurrentHashMap<>();

    private final static Set<Receipt> RECEIPTS = new HashSet<>();

    private final static ConcurrentMap<ServerType, List<Class<?>>> AGENTS = new ConcurrentHashMap<>();

    private final static Set<Class<?>> WORKERS = new HashSet<>();

    private final static Set<Cliff> WALLS = new TreeSet<>();

    private final static Set<Class<?>> POINTER = new HashSet<>();
    private final static Set<Class<?>> DI = new HashSet<>();
    private final static Set<Class<?>> TPS = new HashSet<>();

    private final static Set<Mission> JOBS = new HashSet<>();

    private final static ConcurrentMap<Class<?>, ConcurrentMap<String, Class<?>>> PLUGINS = new ConcurrentHashMap<>();

    public static void prepare(String[] scanPackages) {
        Set<Class<?>> classes = DataTablePack.getClasses(scanPackages);
        Inquirer<Set<Class<?>>> inquirer = ReflectionUtils.singleton(EndPointInquirer.class);
        ENDPOINTS.addAll(inquirer.scan(classes));

        CubeFn.safeSemi(!ENDPOINTS.isEmpty(), () -> {
            final Inquirer<Set<Event>> event = ReflectionUtils.singleton(EventInquirer.class);
            EVENTS.addAll(event.scan(ENDPOINTS));
        }, LOGGER);

        EVENTS.stream().filter(Objects::nonNull)
                .filter(item -> 0 < item.getPath().indexOf(":"))
                .forEach(PathResolver::resolve);

        final Inquirer<ConcurrentMap<String, Set<Event>>> filters = ReflectionUtils.singleton(FilterInquirer.class);
        FILTERS.putAll(filters.scan(classes));

        inquirer = ReflectionUtils.singleton(ConsumerInquirer.class);
        final Set<Class<?>> consumers = inquirer.scan(classes);
        CubeFn.safeSemi(!consumers.isEmpty(),
                () -> {
                    final Inquirer<Set<Receipt>> receipt = ReflectionUtils.singleton(ReceiptInquirer.class);
                    RECEIPTS.addAll(receipt.scan(consumers));
                }, LOGGER);

        final Inquirer<ConcurrentMap<ServerType, List<Class<?>>>> agent = ReflectionUtils.singleton(AgentInquirer.class);
        AGENTS.putAll(agent.scan(classes));


        final Inquirer<Set<Class<?>>> worker = ReflectionUtils.singleton(WorkerInquirer.class);
        WORKERS.addAll(worker.scan(classes));


        final Inquirer<Set<Cliff>> walls = ReflectionUtils.singleton(WallInquirer.class);
        WALLS.addAll(walls.scan(classes));

        final Inquirer<ConcurrentMap<Class<?>, ConcurrentMap<String, Class<?>>>> afflux = ReflectionUtils.singleton(AffluxInquirer.class);
        PLUGINS.putAll(afflux.scan(classes));

        final Inquirer<Set<Class<?>>> pointerInquirer = ReflectionUtils.singleton(PointerInquirer.class);
        POINTER.addAll(pointerInquirer.scan(classes));

        final Inquirer<Set<Class<?>>> diInquirer = ReflectionUtils.singleton(DiInquirer.class);
        DI.addAll(diInquirer.scan(classes));

        final Inquirer<Set<Class<?>>> pluginInquirer = ReflectionUtils.singleton(PluginInquirer.class);
        TPS.addAll(pluginInquirer.scan(classes));

        final Inquirer<Set<Mission>> jobs = ReflectionUtils.singleton(JobInquirer.class);
        JOBS.addAll(jobs.scan(classes));

        final Inquirer<Set<Hook>> initialInquirer = ReflectionUtils.singleton(InitialInquirer.class);
        INITIAL.addAll(initialInquirer.scan(classes));

        final Inquirer<Set<Hook>> destroyInquirer = ReflectionUtils.singleton(DestroyInquirer.class);
        DESTROY.addAll(destroyInquirer.scan(classes));
    }

    public static Set<Class<?>> getEndpoints() {
        return ENDPOINTS;
    }

    public static ConcurrentMap<String, Set<Event>> getFilters(){
        return FILTERS;
    }

    public static Set<Receipt> getReceipts(){
        return RECEIPTS;
    }

    public static ConcurrentMap<ServerType, List<Class<?>>> getAgents(){
        return AGENTS;
    }

    public static Set<Class<?>> getWorkers(){
        return WORKERS;
    }

    public static Set<Event> getEvents(){
        return EVENTS;
    }

    public static Set<Cliff> getWalls(){
        return WALLS;
    }

    public static ConcurrentMap<Class<?>, ConcurrentMap<String, Class<?>>> getPlugins(){
        return PLUGINS;
    }

    public static Set<Class<?>> getPointer(){
        return POINTER;
    }

    public static Set<Class<?>> getTps(){
        return TPS;
    }

    public static Set<Mission> getJobs(){
        return JOBS;
    }

    public static Set<Class<?>> getDi(){
        return DI;
    }

    public static Set<Hook> getInitial(){
        return INITIAL;
    }

    public static Set<Hook> getDestroy(){
        return DESTROY;
    }

    public static String recoveryUri(final String uri, final HttpMethod method) {
        return PathResolver.recovery(uri, method);
    }




}
