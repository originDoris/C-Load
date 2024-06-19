package com.cl.framework.core.web.core.job;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.worker.Mission;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;


/**
 * 扩展job存储器
 * @Author: xhz
 */
class ExtensionStore implements JobStore {
    private static final JobConfig CONFIG = JobPin.getConfig();
    private transient JobStore reference;
    private transient boolean isExtension;

    ExtensionStore() {
        if (Objects.nonNull(CONFIG)) {
            final Class<?> storeCls = CONFIG.getStore().getComponent();
            Optional.ofNullable(storeCls).ifPresent(clazz -> {
                reference = (JobStore) ReflectionUtils.newInstance(clazz);
                isExtension = true;
            });
        }
    }

    @Override
    public Set<Mission> fetch() {
        return extensionCall(HashSet::new, () -> reference.fetch());
    }

    @Override
    public Mission fetch(final String name) {
        return extensionCall(() -> null, () -> reference.fetch(name));
    }

    @Override
    public JobStore remove(final Mission mission) {
        if (isExtension) {
            reference.remove(mission);
        }
        return this;
    }

    @Override
    public JobStore update(final Mission mission) {
        if (isExtension) {
            reference.update(mission);
        }
        return this;
    }

    @Override
    public JobStore add(final Mission mission) {
        if (isExtension) {
            reference.add(mission);
        }
        return this;
    }

    private <T> T extensionCall(final Supplier<T> defaultSupplier, final Supplier<T> extension) {
        if (isExtension) {
            return extension.get();
        } else {
            return defaultSupplier.get();
        }
    }
}
