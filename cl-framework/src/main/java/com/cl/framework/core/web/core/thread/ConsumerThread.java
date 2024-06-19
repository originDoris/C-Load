package com.cl.framework.core.web.core.thread;

import com.cl.framework.core.constants.MessageConstant;
import com.cl.framework.core.utils.reflection.ReflectionUtils;
import com.cl.framework.core.web.core.Extractor;
import com.cl.framework.core.web.core.worker.ReceiptExtractor;
import com.cl.framework.core.web.core.inquirer.ReceiptInquirer;
import com.cl.framework.core.web.core.worker.Receipt;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * 将Consumer 转换为Receipt
 *
 * @author xhz
 */
public class ConsumerThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptInquirer.class);

    @Getter
    private final Set<Receipt> receipts = new HashSet<>();

    private final transient Extractor<Set<Receipt>> extractor = ReflectionUtils.newInstance(ReceiptExtractor.class);

    private final transient Class<?> reference;

    public ConsumerThread(final Class<?> clazz) {
        this.setName("cl.consumer-scanner-" + this.getId());
        this.reference = clazz;
    }

    @Override
    public void run() {
        if (null != this.reference) {
            this.receipts.addAll(this.extractor.extract(this.reference));
            LOGGER.info(MessageFormat.format(MessageConstant.SCAN_RECEIPTS, this.reference.getName(), this.receipts.size()));
        }
    }

}
