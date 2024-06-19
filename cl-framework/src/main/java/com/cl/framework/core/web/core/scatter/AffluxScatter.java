package com.cl.framework.core.web.core.scatter;



import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.runtime.DataTableAnno;
import com.cl.framework.core.runtime.Runner;
import com.cl.framework.core.web.core.agent.Event;
import com.cl.framework.core.web.core.di.DiScanner;
import com.cl.framework.core.web.core.worker.Receipt;
import io.vertx.rxjava3.core.Vertx;

import java.util.Set;

/**
 * Injection system
 * @author xhz
 */
public class AffluxScatter implements Scatter<Vertx> {
    @Override
    public void connect(final Vertx vertx) {
        final DiScanner injector = DiScanner.create(this.getClass());
        // Extract all events.
        final Set<Event> events = DataTableAnno.getEvents();
        CubeFn.itSet(events, (item, index) -> Runner.run(() -> injector.singleton(item.getProxy()), "event-afflux-" + index));

        // Extract all receipts.
        final Set<Receipt> receipts = DataTableAnno.getReceipts();
        CubeFn.itSet(receipts, (item, index) -> Runner.run(() -> injector.singleton(item.getProxy()), "receipt-afflux-" + index));

        // Extract non - event/receipts Objects
        final Set<Class<?>> injects = DataTableAnno.getPointer();
        CubeFn.itSet(injects, (item, index) -> Runner.run(() -> injector.singleton(item), "injects-afflux-" + index));

        final Set<Class<?>> di = DataTableAnno.getDi();
        CubeFn.itSet(di, (item, index) -> Runner.run(() -> injector.singleton(item), "di-afflux-" + index));
    }
}
