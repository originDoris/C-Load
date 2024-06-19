package com.cl.framework.core.runtime;

import com.cl.framework.core.enums.ErrorCodeEnum;
import com.cl.framework.core.exception.WebException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 信封
 * 统一数据模型 - 融合了Request-Response过程中所有牵涉的核心数据
 *
 * @author xhz
 */
@Getter
@Setter
public class Envelop implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Envelop.class);

    private com.cl.framework.core.exception.DataTableException error;

    private JsonObject data;

    private final JsonObject cachedJwt = new JsonObject();

    private String key;

    @JsonIgnore
    private final transient Assist assist = new Assist();

    private ErrorCodeEnum status;

    private <T> Envelop(final T data, final ErrorCodeEnum status) {
        final Object serialized = DataTableSerializer.toSupport(data);
        final JsonObject bodyData = new JsonObject();
        bodyData.put("data", serialized);
        this.data = bodyData;
        this.error = null;
        this.status = status;
    }

    private Envelop(final com.cl.framework.core.exception.DataTableException error) {
        this.error = error;
        this.data = error.toJson();
        this.status = error.getErrorCodeEnum();
    }

    public static Envelop failure(final com.cl.framework.core.exception.DataTableException error) {
        LOGGER.warn(error.getMessage());
        return new Envelop(error);
    }

    public static Envelop failure(final Throwable ex) {
        if (ex instanceof WebException) {
            // Throwable converted to WebException
            return failure((WebException) ex);
        } else {
            return new Envelop(new WebException(ErrorCodeEnum.INTERNAL_ERROR, ex.getMessage()));
        }
    }

    public static <T> Envelop success(final T entity) {
        return new Envelop(entity, ErrorCodeEnum.SUCCESS);
    }


    public Envelop bind(final RoutingContext context) {
        HttpServerRequest request = context.request();

        /* Http Request Part */
        this.assist.setHeaders(request.headers());
        this.assist.setUri(request.uri());
        this.assist.setMethod(request.method());

        /* Session, User, Data */
        this.assist.setSession(context.session());
        this.assist.setUser(context.user());
        this.assist.setContext(context.data());
        return this;
    }

    public boolean valid() {
        return null == this.error;
    }


    public static Envelop ok() {
        return new Envelop(null, ErrorCodeEnum.SUCCESS);
    }

    public String outString() {
        return this.outJson() == null ? "" : this.outJson().toString();
    }

    /* Json */
    public Object outJson() {
        if (Objects.isNull(error)) {
            return data.getValue("data");
        } else {
            return error.toJson();
        }
    }

    public Buffer outBuffer() {
        if (Objects.isNull(error)) {
            final JsonObject response = data.getJsonObject("data");
            return Buffer.buffer(response.getBinary("bytes"));
        } else {
            final JsonObject errorJson = error.toJson();
            return Buffer.buffer(errorJson.encode());
        }
    }

    public static Envelop okJson() {
        return new Envelop(new JsonObject(), ErrorCodeEnum.SUCCESS);
    }


    public Envelop() {
    }
}
