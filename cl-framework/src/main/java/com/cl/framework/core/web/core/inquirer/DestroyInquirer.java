package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.agent.Hook;
import com.cl.framework.core.web.core.thread.DestroyThread;
import com.cl.framework.core.web.core.thread.InitialThread;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 扫描Destroy方法
 *
 * @author xhz
 */
@Slf4j
public class DestroyInquirer implements Inquirer<Set<Hook>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestroyInquirer.class);
    @Override
    public Set<Hook> scan(Set<Class<?>> classes) {
        final List<DestroyThread> threadReference = new ArrayList<>();
        for (final Class<?> clazz : classes) {
            final DestroyThread thread = new DestroyThread(clazz);
            threadReference.add(thread);
            thread.start();
        }
        CubeFn.safeJvm(() -> {
            for (final DestroyThread item : threadReference) {
                item.join();
            }
        }, LOGGER);
        final Set<Hook> hooks = new HashSet<>();
        CubeFn.safeJvm(() -> {
            for (final DestroyThread item : threadReference) {
                hooks.addAll(item.getHooks());
            }
        }, LOGGER);
        return hooks;
    }
}
