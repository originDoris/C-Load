package com.cl.framework.core.web.core.inquirer;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.web.core.thread.ConsumerThread;
import com.cl.framework.core.web.core.worker.Receipt;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 将Consumer扫描到的数据转换为Receipt元数据
 *
 * @author xhz
 */
public class ReceiptInquirer implements Inquirer<Set<Receipt>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptInquirer.class);

    @Override
    public Set<Receipt> scan(final Set<Class<?>> consumers) {
        final List<ConsumerThread> threadReference = new ArrayList<>();

        for (final Class<?> consumer : consumers) {
            final ConsumerThread thread = new ConsumerThread(consumer);
            threadReference.add(thread);
            thread.start();
        }

        CubeFn.safeJvm(() -> {
            for (final ConsumerThread item : threadReference) {
                item.join();
            }
        }, LOGGER);

        final Set<Receipt> receipts = new HashSet<>();
        CubeFn.safeJvm(() -> threadReference.stream()
                .map(ConsumerThread::getReceipts)
                .forEach(receipts::addAll), LOGGER);
        return receipts;
    }
}
