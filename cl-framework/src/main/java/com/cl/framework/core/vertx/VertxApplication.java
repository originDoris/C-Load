package com.cl.framework.core.vertx;


import com.cl.framework.core.annotation.Anno;
import com.cl.framework.core.annotation.StartUp;
import com.cl.framework.core.constants.ErrorInfoConstant;
import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Runner;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.agent.Hook;
import com.cl.framework.core.web.core.scatter.*;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * vertx 启动类 加载框架的关键组件，启动入口
 *
 * @author xhz
 */
@Slf4j
public class VertxApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxApplication.class);


    private transient final Class<?> clazz;

    public static String[] scanBasePackages;



    private VertxApplication(final Class<?> clazz) {
        if (clazz == null) {
            throw new com.cl.framework.core.exception.DataTableException(ErrorCodeEnum.START_UP_FAILURE, ErrorInfoConstant.START_UP_CLASS_NULL_MSG);
        }

        this.clazz = clazz;
    }


    public static void run(final Class<?> clazz, final Object... args) {
        CubeFn.onRun(LOGGER, () -> {
            ConcurrentMap<String, Annotation> startAnnoMap = Anno.get(clazz);
            if (!startAnnoMap.containsKey(StartUp.class.getName())) {
                throw new com.cl.framework.core.exception.DataTableException(ErrorCodeEnum.START_UP_FAILURE, MessageFormat.format(ErrorInfoConstant.START_UP_ANNO_IS_NULL, StartUp.class.getName()));
            }
            StartUp startUp = Anno.getStartUp(startAnnoMap.get(StartUp.class.getName()));
            scanBasePackages = startUp.scanBasePackages();

            com.cl.framework.core.runtime.DataTableAnno.prepare(scanBasePackages);

            new VertxApplication(clazz).run(args);
        });
    }

    private void run(final Object... args) {


        VertxLauncher.initLauncher().start(rxVertx -> {
            if (com.cl.framework.core.runtime.DataTableConfig.getFlywayOptions().isEnable()) {
                Flyway.configure().configuration(com.cl.framework.core.runtime.DataTableConfig.getFlywayOptions().getMap()).load().migrate();
            }
            com.cl.framework.core.runtime.DataTableMotor.codec(rxVertx.eventBus());
            try {
                Thread main = Runner.run(() -> {
                    // Infix
                    Scatter<Vertx> scatter = ReflectionUtils.singleton(InfixScatter.class);
                    scatter.connect(rxVertx);
                    // Injection
                    scatter = ReflectionUtils.singleton(AffluxScatter.class);
                    scatter.connect(rxVertx);

                    // 部署Agent
                    Thread agentRun = Runner.run(() -> {
                        AgentScatter agentScatter = ReflectionUtils.singleton(AgentScatter.class);
                        agentScatter.connect(rxVertx);
                    }, "agent-runner");

                    // 部署Worker
                    Thread workerRun = Runner.run(() -> {
                        final Scatter<Vertx> workerScatter = ReflectionUtils.singleton(WorkerScatter.class);
                        workerScatter.connect(rxVertx);
                    }, "worker-runner");

                    try {
                        agentRun.join();
                        workerRun.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }, "infix-afflux-runner");
                main.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            //执行 initial
            Set<Hook> initial = com.cl.framework.core.runtime.DataTableAnno.getInitial();
            execHook(initial);

        }, com.cl.framework.core.runtime.DataTableConfig.getVxOpts());
    }


    public static void execHook(Set<Hook> hooks) {
        try {
            List<Hook> hookList = new ArrayList<>(hooks);
            hookList.sort(Comparator.comparingInt(Hook::getOrder));
            for (Hook hook : hookList) {
                ReflectionUtils.invokeMethod(hook.getAction(), ReflectionUtils.singleton(hook.getAClass()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }




}
