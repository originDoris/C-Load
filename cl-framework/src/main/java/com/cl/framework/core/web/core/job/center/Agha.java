package com.cl.framework.core.web.core.job.center;

import com.cl.framework.core.enums.JobType;
import com.cl.framework.core.web.core.Pool;
import com.cl.framework.core.web.core.worker.Mission;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Future;


/**
 * job管理器
 * @author xhz
 */
public interface Agha {

    static Agha get(final JobType type) {
        return Pool.AGHAS.getOrDefault(type, new PlanAgha());
    }


    Single<Long> begin(Mission mission);
}
