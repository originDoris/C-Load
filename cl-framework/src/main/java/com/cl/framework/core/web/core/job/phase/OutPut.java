package com.cl.framework.core.web.core.job.phase;


import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.exception.DataTableException;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.worker.Mission;
import com.cl.framework.plugin.job.JobOutcome;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.rxjava3.core.Promise;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Objects;

class OutPut {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutPut.class);
    private transient final Vertx vertx;
    private transient final Refer assist = new Refer();

    OutPut(final Vertx vertx) {
        this.vertx = vertx;
    }

    OutPut bind(final Refer assist) {
        if (Objects.nonNull(assist)) {
            this.assist.add(assist.get());
        }
        return this;
    }

    Single<Envelop> outcomeAsync(final Envelop envelop, final Mission mission) {
        if (envelop.valid()) {
            final JobOutcome outcome = Element.outcome(mission);
            if (Objects.isNull(outcome)) {
                Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_4TH_JOB, mission.getCode())));

                return Single.just(envelop);
            } else {
                LOGGER.info(MessageFormat.format(MessageConstant.JOB_COMPONENT_SELECTED, "JobOutcome", outcome.getClass().getName()));
                ReflectionUtils.contract(OutPut.class, outcome, Mission.class, mission, LOGGER);

                Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_4TH_JOB_ASYNC, mission.getCode(), outcome.getClass().getName())));
                return outcome.afterAsync(envelop);
            }
        } else {
            Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_ERROR, mission.getCode(), envelop.getError().getClass().getName())));
            final DataTableException error = envelop.getError();
            error.printStackTrace();
            return Single.just(envelop);
        }
    }

    Single<Envelop> outputAsync(final Envelop envelop, final Mission mission) {
        if (envelop.valid()) {
            final String address = mission.getOutcomeAddress();
            if (StringUtils.isBlank(address)) {
                Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_5TH_JOB, mission.getCode())));
                return Single.just(envelop);
            } else {
                LOGGER.info(MessageFormat.format(MessageConstant.JOB_ADDRESS_EVENT_BUS, "Outcome", address));
                final Promise<Envelop> output = Promise.promise();
                final EventBus eventBus = this.vertx.eventBus();
                Element.onceLog(mission,
                        () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_5TH_JOB_ASYNC, mission.getCode(), address)));
                eventBus.<Envelop>request(address, envelop, delivery())
                        .subscribe(message -> {
                            Envelop body = message.body();
                            output.complete(body);
                        },throwable -> {
                            output.complete(Envelop.failure(throwable.getCause()));
                        });
                return Single.just(output.future().result());
            }
        } else {
            Element.onceLog(mission, () -> LOGGER.info(MessageFormat.format(MessageConstant.PHASE_ERROR, mission.getCode(), envelop.getError().getClass().getName())));
            return Single.just(envelop);
        }
    }

    public DeliveryOptions delivery() {
        final DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(600000);
        return options;
    }
}
