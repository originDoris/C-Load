package com.cl.framework.plugin.jooq.shared.postgres;

import io.vertx.core.json.JsonObject;
import org.jooq.Converter;
import org.jooq.JSONB;

import java.util.function.Function;

/**
 * @author xhz
 */
public class JSONBToJsonObjectBinding extends PGJsonToVertxJsonBinding<JSONB, JsonObject> {

    @Override
    public Converter<JSONB, JsonObject> converter() {
        return JSONBToJsonObjectConverter.getInstance();
    }

    @Override
    Function<String, JSONB> valueOf() {
        return JSONB::valueOf;
    }

    @Override
    String coerce() {
        return "::jsonb";
    }

}
