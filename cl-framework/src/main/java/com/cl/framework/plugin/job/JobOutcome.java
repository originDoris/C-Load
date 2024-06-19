package com.cl.framework.plugin.job;

import com.cl.framework.core.runtime.Envelop;
import io.reactivex.rxjava3.core.Single;

/**
 * job 执行结果
 * @author xhz
 */
public interface JobOutcome {
    /*
     * Async process outcome here
     */
    Single<Envelop> afterAsync(final Envelop envelop);
}
