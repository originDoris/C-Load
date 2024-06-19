package com.cl.framework.core.web.core;

import com.cl.framework.core.enums.JobType;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.di.DiPlugin;
import com.cl.framework.core.web.core.di.DiScanner;
import com.cl.framework.core.web.core.job.center.Agha;
import com.cl.framework.core.web.core.job.center.FixedAgha;
import com.cl.framework.core.web.core.job.center.OnceAgha;
import com.cl.framework.core.web.core.job.center.PlanAgha;
import com.cl.framework.core.web.core.job.phase.Phase;
import com.cl.framework.core.web.core.route.adaptor.BufferWings;
import com.cl.framework.core.web.core.route.adaptor.JsonWings;
import com.cl.framework.core.web.core.route.adaptor.Wings;
import com.cl.framework.core.web.core.route.axis.Axis;
import com.cl.framework.core.web.core.route.Hub;
import com.cl.framework.core.web.core.route.aim.Aim;
import com.cl.framework.core.web.core.route.differ.ModeSplitter;
import com.cl.framework.core.web.core.secure.Cliff;
import com.cl.framework.plugin.job.JobIncome;
import com.cl.framework.plugin.job.JobOutcome;
import io.vertx.rxjava3.ext.web.Route;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;

import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xhz
 */
public interface Pool {

    ConcurrentMap<String, Axis<Router>> ROUTERS = new ConcurrentHashMap<>();

    ConcurrentMap<String, Axis<Router>> EVENTS = new ConcurrentHashMap<>();

    ConcurrentMap<String, Axis<Router>> FILTERS = new ConcurrentHashMap<>();

    ConcurrentMap<String, Axis<Router>> WALLS = new ConcurrentHashMap<>();
    ConcurrentMap<String, Hub<Route>> URIHUBS = new ConcurrentHashMap<>();

    ConcurrentMap<String, Hub<Route>> MEDIAHUBS = new ConcurrentHashMap<>();

    ConcurrentMap<String, Set<Cliff>> WALL_MAP = new ConcurrentHashMap<>();

    ConcurrentMap<String, ModeSplitter> THREADS = new ConcurrentHashMap<>();

    ConcurrentMap<String, Aim<RoutingContext>> AIMS = new ConcurrentHashMap<>();

    ConcurrentMap<String, Axis<Router>> DYNAMICS = new ConcurrentHashMap<>();

    ConcurrentMap<Class<?>, DiPlugin> PLUGINS = new ConcurrentHashMap<>();
    ConcurrentMap<Class<?>, DiScanner> INJECTION = new ConcurrentHashMap<>();

    ConcurrentMap<String, Class<?>> CLASSES = new ConcurrentHashMap<>();

    ConcurrentMap<String, ConcurrentMap<String, Wings>> SELECT_POOL = new ConcurrentHashMap<String, ConcurrentMap<String, Wings>>() {
        {
            this.put(MediaType.WILDCARD_TYPE.getType(), new ConcurrentHashMap<String, Wings>() {
                {
                    this.put(MediaType.WILDCARD_TYPE.getSubtype(), ReflectionUtils.singleton(JsonWings.class));
                }
            });


            this.put(MediaType.APPLICATION_JSON_TYPE.getType(), new ConcurrentHashMap<String, Wings>() {
                {
                    this.put(MediaType.APPLICATION_JSON_TYPE.getSubtype(), ReflectionUtils.singleton(JsonWings.class));
                    this.put(MediaType.APPLICATION_OCTET_STREAM_TYPE.getSubtype(), ReflectionUtils.singleton(BufferWings.class));
                }
            });

        }
    };

    ConcurrentMap<JobType, Agha> AGHAS = new ConcurrentHashMap<JobType, Agha>() {
        {
            put(JobType.FIXED, new FixedAgha());
            put(JobType.ONCE, new OnceAgha());
            put(JobType.PLAN, new PlanAgha());
        }
    };

    ConcurrentMap<String, JobIncome> INCOMES = new ConcurrentHashMap<>();
    ConcurrentMap<String, JobOutcome> OUTCOMES = new ConcurrentHashMap<>();
    ConcurrentMap<String, Phase> PHASES = new ConcurrentHashMap<>();
}