package com.cl.framework.core.web.core.job;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.options.visitor.InjectVisitor;
import com.cl.framework.core.utils.JsonUtil;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.worker.Mission;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import java.text.MessageFormat;

/**
 * Job配置读取
 * @author xhz
 */
public class JobPin {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobPin.class);
    private static final InjectVisitor VISITOR = ReflectionUtils.singleton(InjectVisitor.class);
    private static final String JOB = "job";
    private static JobConfig CONFIG;
    private static JobStore STORE;

    static {
        final JsonObject job = VISITOR.visit(JOB);
        if (job != null && !job.isEmpty()) {
            CONFIG = JsonUtil.deserialize(job.encode(), JobConfig.class);
        }else {
            CONFIG = new JobConfig();
        }
        LOGGER.info(MessageFormat.format(MessageConstant.JOB_CONFIG, CONFIG.toString()));
    }

    public static JobConfig getConfig() {
        return CONFIG;
    }

    public static JobStore getStore() {
        synchronized (JobStore.class) {
            if (null == STORE) {
                STORE = new UnityStore();
            }
            return STORE;
        }
    }

    public static boolean isIn(final Mission mission) {
        return JobPool.valid(mission);
    }
}
