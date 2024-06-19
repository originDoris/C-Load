package com.cl.framework.core.web.core.param.serialization;

import com.cl.framework.core.runtime.Envelop;
import com.cl.framework.core.utils.FieldUtil;
import com.cl.framework.core.web.config.DataTableHeader;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.core.eventbus.EventBus;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.auth.User;
import io.vertx.rxjava3.ext.web.FileUpload;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.Session;

import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * TypedArgument
 * @author xhz
 */
public class TypedArgument {


    public static Object analyze(final Envelop envelop, final Class<?> type) {
        final Object returnValue;
        if (is(type, User.class)) {
            returnValue = envelop.getAssist().getUser();
        } else if (is(type, Session.class)) {
            returnValue = envelop.getAssist().getSession();
        } else if (is(type, DataTableHeader.class)) {
            MultiMap headers = envelop.getAssist().getHeaders();
            final DataTableHeader header = new DataTableHeader();
            header.fromHeader(headers);
            returnValue = header;
        } else {
            returnValue = null;
        }
        return returnValue;
    }

    public static Object analyze(final RoutingContext context, final Class<?> type) {
        Object returnValue = null;
        if (is(type, DataTableHeader.class)) {
            HttpServerRequest request = context.request();
            MultiMap headers = request.headers();
            final DataTableHeader header = new DataTableHeader();
            header.fromHeader(headers);
            returnValue = header;
        } else if (is(type, Session.class)) {
            returnValue = context.session();
        } else if (is(type, HttpServerRequest.class)) {
            returnValue = context.request();
        } else if (is(type, HttpServerResponse.class)) {
            returnValue = context.response();
        } else if (is(type, Vertx.class)) {
            returnValue = context.vertx();
        } else if (is(type, EventBus.class)) {
            returnValue = context.vertx().eventBus();
        } else if (is(type, User.class)) {
            returnValue = context.user();
        } else if (is(type, Set.class)) {
            returnValue = context.fileUploads();
        } else if (is(type, JsonArray.class)) {
            returnValue = context.body().asJsonArray();
            if (Objects.isNull(returnValue)) {
                returnValue = new JsonArray();
            }
        } else if (is(type, JsonObject.class)) {
            returnValue = context.body().asJsonArray();
            if (Objects.isNull(returnValue)) {
                returnValue = new JsonObject();
            }
        } else if (is(type, Buffer.class)) {
            returnValue = context.body();
            if (Objects.isNull(returnValue)) {
                returnValue = Buffer.buffer();
            }
        } else if (is(type, FileUpload.class)) {
            List<FileUpload> uploads = context.fileUploads();
            if (!uploads.isEmpty()) {
                returnValue = uploads.iterator().next();
            }
        }
        return returnValue;
    }

    private static boolean is(final Class<?> paramType, final Class<?> expected) {
        return expected == paramType || FieldUtil.isImplement(paramType, expected);
    }
}
