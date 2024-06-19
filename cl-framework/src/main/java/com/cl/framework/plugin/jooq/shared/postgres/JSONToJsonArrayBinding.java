package com.cl.framework.plugin.jooq.shared.postgres;

import io.vertx.core.json.JsonArray;
import org.jooq.Converter;
import org.jooq.JSON;

import java.util.function.Function;

/**
 * @author xhz
 */
public class JSONToJsonArrayBinding extends PGJsonToVertxJsonBinding<JSON, JsonArray> {

    @Override
    public Converter<JSON, JsonArray> converter() {
        return JSONToJsonArrayConverter.getInstance();
    }

    @Override
    Function<String, JSON> valueOf() {
        return JSON::valueOf;
    }

    @Override
    String coerce() {
        return "::json";
    }

}
