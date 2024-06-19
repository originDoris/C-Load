package com.cl.framework.core.web.core.job;



import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.worker.Mission;

import java.util.Set;


/**
 * CodeStore
 * @author xhz
 */
class CodeStore implements JobReader {
    private static final Set<Mission> MISSIONS = com.cl.framework.core.runtime.DataTableAnno.getJobs();

    @Override
    public Set<Mission> fetch() {
        return MISSIONS;
    }

    @Override
    public Mission fetch(final String code) {
        return CubeFn.getDefault(null, () -> MISSIONS.stream()
                .filter(mission -> code.equals(mission.getCode()))
                .findFirst().orElse(null), code);
    }
}
