package com.cl.framework.core.runtime;

import brave.Span;
import com.cl.framework.core.funcation.SafeSupplier;
import io.vertx.tracing.zipkin.ZipkinTracer;

/**
 * ZipKin
 *
 * @author xhz
 */
public class Zipkin {

    public static <T> T trace(final SafeSupplier<T> actuator, String remoteServiceName, String component, String dbType) {
        Span span = ZipkinTracer.activeSpan();
        span.tag("component", component);
        span.tag("dbType", dbType);
        span.remoteServiceName(remoteServiceName);
        try {
            return actuator.get();
        } catch (Exception e) {
            span.error(e);
            throw new RuntimeException(e);
        } finally {
            span.finish();
        }
    }


    public static <T> T pgTrace(final SafeSupplier<T> actuator, String remoteServiceName){
        return trace(actuator, remoteServiceName, "pg", "sql");
    }
}
