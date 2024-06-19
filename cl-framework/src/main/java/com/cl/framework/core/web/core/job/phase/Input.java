package com.cl.framework.core.web.core.job.phase;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.worker.Mission;
import com.cl.framework.plugin.job.JobIncome;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.Promise;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Objects;

class Input {

    private static final Logger LOGGER = LoggerFactory.getLogger(Input.class);

    private transient final Vertx vertx;
    private transient final Refer underway = new Refer();

    Input(final Vertx vertx) {
        this.vertx = vertx;
    }

    Refer underway() {
        return this.underway;
    }

    Single<Envelop> inputAsync(final Mission mission) {
        final String address = mission.getIncomeAddress();
        if (StringUtils.isBlank(address)) {
            Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_1ST_JOB, mission.getCode())));
            return Single.just(Envelop.okJson());
        } else {
            LOGGER.info(MessageFormat.format(MessageConstant.JOB_ADDRESS_EVENT_BUS, "Income", address));
            final Promise<Envelop> input = Promise.promise();
            final EventBus eventBus = this.vertx.eventBus();
            eventBus.<Envelop>consumer(address, handler -> {

                Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_1ST_JOB_ASYNC, mission.getCode(), address)));

                final Envelop envelop = handler.body();
                if (Objects.isNull(envelop)) {
                    input.complete(Envelop.ok());
                } else {
                    input.complete(envelop);
                }
            });
            return Single.just(input.future().result());
        }
    }

    Single<Envelop> incomeAsync(final Envelop envelop, final Mission mission) {
        if (envelop.valid()) {
            final JobIncome income = Element.income(mission);
            if (Objects.isNull(income)) {
                Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_2ND_JOB, mission.getCode())));
                return Single.just(envelop);
            } else {
                LOGGER.info(MessageFormat.format(MessageConstant.JOB_COMPONENT_SELECTED, "JobIncome", income.getClass().getName()));
                ReflectionUtils.contract(Input.class, income, Mission.class, mission, LOGGER);
                Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_2ND_JOB_ASYNC, mission.getCode(), income.getClass().getName())));

                return income.underway().flatMap(refer -> {
                    this.underway.add(refer.get());
                    return income.beforeAsync(envelop);
                });
            }
        } else {
            Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_ERROR, mission.getCode(), envelop.getError().getClass().getName())));
            return Single.just(envelop);
        }
    }
}
