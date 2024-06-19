package com.cl.framework.core.web.core.scatter;

import com.cl.framework.core.enums.ServerType;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.utils.VerticleUtils;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.vertx.VertxApplication;
import com.cl.framework.core.vertx.VertxLauncher;
import com.cl.framework.core.web.core.agent.AgentExtractor;
import com.cl.framework.core.web.core.Extractor;
import com.cl.framework.core.web.core.Factor;
import com.cl.framework.core.web.core.HttpFactor;
import com.cl.framework.core.web.core.agent.Hook;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.Vertx;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Vertx 部署Agent
 *
 * @author xhz
 */
public class AgentScatter implements Scatter<Vertx>, Serializable {


    private static final Logger LOGGER = LoggerFactory.getLogger(AgentScatter.class);


    private transient final Factor factor = ReflectionUtils.singleton(HttpFactor.class);

    @Override
    public void connect(Vertx vertx) {
        final ConcurrentMap<ServerType, Class<?>> agents = this.factor.agents();
        final Extractor<DeploymentOptions> extractor = ReflectionUtils.newInstance(AgentExtractor.class);
        final ConcurrentMap<Class<?>, DeploymentOptions> options = new ConcurrentHashMap<>();

        for (Map.Entry<ServerType, Class<?>> entry : agents.entrySet()) {
            final DeploymentOptions option = extractor.extract(entry.getValue());
            options.put(entry.getValue(), option);
            VerticleUtils.deploy(vertx, entry.getValue(), option, LOGGER);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Set<Hook> destroy = DataTableAnno.getDestroy();
            VertxApplication.execHook(destroy);

            for (Map.Entry<ServerType, Class<?>> entry : agents.entrySet()) {
                final DeploymentOptions opt = options.get(entry.getValue());
                VerticleUtils.undeploy(vertx, entry.getValue(), opt, LOGGER);
            }
        }));
    }
}
