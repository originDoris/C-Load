package com.cl.framework.core.web.core.scatter;

import com.cl.framework.core.annotation.Worker;
import com.cl.framework.core.enums.MessageModel;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.utils.VerticleUtils;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.Extractor;
import com.cl.framework.core.web.core.job.DataTableScheduler;
import com.cl.framework.core.web.core.worker.WorkerExtractor;
import com.cl.framework.core.web.core.worker.DataTableHttpWorker;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.Vertx;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 扫描并部署Worker节点
 * @author xhz
 */
public class WorkerScatter implements Scatter<Vertx> {

    @Override
    public void connect(final Vertx vertx) {
        final Set<Class<?>> sources = DataTableAnno.getWorkers();
        sources.add(DataTableHttpWorker.class);
        sources.add(DataTableScheduler.class);

        final Set<Class<?>> workers = this.getTargets(sources);
        final Extractor<DeploymentOptions> extractor = ReflectionUtils.newInstance(WorkerExtractor.class);
        final ConcurrentMap<Class<?>, DeploymentOptions> options = new ConcurrentHashMap<>();
        for (final Class<?> worker : workers) {
            final DeploymentOptions option = extractor.extract(worker);
            options.put(worker, option);
            VerticleUtils.deploy(vertx,worker, option, this.getLogger());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                CubeFn.itSet(workers, (clazz, index) -> {
                    final DeploymentOptions opt = options.get(clazz);
                    VerticleUtils.undeploy(vertx, clazz, opt, this.getLogger());
                })));
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    private Set<Class<?>> getTargets(final Set<Class<?>> sources) {
        final Set<Class<?>> workers = new HashSet<>();
        for (final Class<?> source : sources) {
            final MessageModel model = ReflectionUtils.invoke(source.getAnnotation(Worker.class), "value");
            if (this.getModel().contains(model)) {
                workers.add(source);
            }
        }
        return workers;
    }

    protected Set<MessageModel> getModel() {
        return new HashSet<MessageModel>() {{
                this.add(MessageModel.REQUEST_RESPONSE);
            }
        };
    }

}
