package com.cl.framework.core.web.core.job.phase;


import com.cl.framework.core.enums.JobType;
import com.cl.framework.core.funcation.Actuator;
import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.funcation.SafeActuator;
import com.cl.framework.core.runtime.Runner;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.Pool;
import com.cl.framework.core.web.core.worker.Mission;
import com.cl.framework.plugin.job.JobIncome;
import com.cl.framework.plugin.job.JobOutcome;

import java.util.Objects;

/*
 * 协助
 */
class Element {

    static JobIncome income(final Mission mission) {
        final Class<?> incomeCls = mission.getIncome();
        JobIncome income = null;
        if (Objects.nonNull(incomeCls) && ReflectionUtils.isMatch(incomeCls, JobIncome.class)) {
            income = CubeFn.pool(Pool.INCOMES, mission.getCode(), () -> (JobIncome) ReflectionUtils.newInstance(incomeCls));
        }
        return income;
    }

    static JobOutcome outcome(final Mission mission) {
        final Class<?> outcomeCls = mission.getOutcome();
        JobOutcome outcome = null;
        if (Objects.nonNull(outcomeCls) && ReflectionUtils.isMatch(outcomeCls, JobOutcome.class)) {
            outcome = CubeFn.pool(Pool.OUTCOMES, mission.getCode(), () ->  (JobOutcome) ReflectionUtils.newInstance(outcomeCls));
        }
        return outcome;
    }

    static void onceLog(final Mission mission, final SafeActuator actuator) {
        if (JobType.ONCE == mission.getType()) {
            Runner.run(actuator::execute, "once-logger-debug");
        }
    }
}
