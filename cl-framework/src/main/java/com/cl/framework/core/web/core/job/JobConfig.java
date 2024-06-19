package com.cl.framework.core.web.core.job;


import com.cl.framework.core.web.core.job.timer.VertxInterval;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * job:
 * - store:
 *   - component:
 *   - config:
 * - interval:
 *   - component:
 *   - config:
 * - client:
 *   - config:
 * @author xhz
 */
public class JobConfig implements Serializable {

    private transient ComponentOpts store;
    private transient ComponentOpts interval;
    private transient ComponentOpts client;

    public ComponentOpts getStore() {
        return Optional.ofNullable(store).orElse(new ComponentOpts());
    }

    public void setStore(final ComponentOpts store) {
        this.store = store;
    }

    public ComponentOpts getInterval() {
        final ComponentOpts componentOpts = Optional.ofNullable(interval).orElse(new ComponentOpts());
        if (Objects.isNull(componentOpts.getComponent())) {
            componentOpts.setComponent(VertxInterval.class);
        }
        return componentOpts;
    }

    public void setInterval(final ComponentOpts interval) {
        this.interval = interval;
    }

    public ComponentOpts getClient() {
        return Optional.ofNullable(client).orElse(new ComponentOpts());
    }

    public void setClient(final ComponentOpts client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "JobConfig{" +
                "store=" + store +
                ", interval=" + interval +
                ", client=" + client +
                '}';
    }
}
