package com.cl.framework.core.web.core.job.phase;


import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.web.core.Pool;
import com.cl.framework.core.web.core.worker.Mission;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.Vertx;


public class Phase {

    private transient Vertx vertx;
    private transient Mission mission;
    private transient Input input;
    private transient RunOn runOn;
    private transient OutPut output;

    private Phase() {
    }

    public static Phase start(final String name) {
        return CubeFn.pool(Pool.PHASES, name, Phase::new);
    }

    public Phase bind(final Vertx vertx) {
        this.vertx = vertx;
        this.input = new Input(this.vertx);
        this.runOn = new RunOn();
        this.output = new OutPut(this.vertx);
        return this;
    }

    public Phase bind(final Mission mission) {
        this.mission = mission;
        return this;
    }

    public Single<Envelop> inputAsync(final Mission mission) {
        return this.input.inputAsync(mission);
    }

    public Single<Envelop> incomeAsync(final Envelop envelop) {
        return this.input.incomeAsync(envelop, this.mission);
    }

    public Single<Envelop> invokeAsync(final Envelop envelop) {
        return this.runOn.bind(this.input.underway())
                .invoke(envelop, this.mission);
    }

    public Single<Envelop> outcomeAsync(final Envelop envelop) {
        return this.output.outcomeAsync(envelop, this.mission);
    }

    public Single<Envelop> outputAsync(final Envelop envelop) {
        return this.output.bind(this.input.underway())
                .outputAsync(envelop, this.mission);
    }

    public Single<Envelop> callbackAsync(final Envelop envelop) {
        return this.runOn.callback(envelop, this.mission);
    }
}
