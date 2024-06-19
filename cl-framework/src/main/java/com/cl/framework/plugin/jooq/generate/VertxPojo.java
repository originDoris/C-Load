package com.cl.framework.plugin.jooq.generate;


import io.vertx.core.json.JsonObject;

import java.util.function.Consumer;
import java.util.function.Function;

public interface VertxPojo {

    public VertxPojo fromJson(JsonObject json);

    public JsonObject toJson();

    public static <T> void setOrThrow(Consumer<T> pojoSetter, Function<String,T> jsonGetter, String fieldName, String expectedFieldType){
        try {
            pojoSetter.accept(jsonGetter.apply(fieldName));
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}
