package com.cl.framework.core.utils;

import com.cl.framework.core.funcation.CubeFn;
import com.cl.framework.core.utils.jackson.DataTableModule;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xhz
 * JsonUtil
 */
public class JsonUtil {

    public static ObjectMapper objectMapper = DatabindCodec.mapper();

    static {

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new DataTableModule());
    }
    public static JsonObject toJsonObject(Object o) {
        return JsonObject.mapFrom(o);
    }

    public static JsonObject stringToJsonObject(String json){
        return new JsonObject(json);
    }

    public static JsonArray stringToJsonArray(String json){
        return new JsonArray(json);
    }

    public static JsonArray toJsonArray(Object o){
       return new JsonArray(toJson(o));
    }

    public static String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(final String value, final Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T toObject(Object o, Class<T> tClass) {
        return toObject(JsonUtil.stringToJsonObject(JsonUtil.toJson(o)), tClass);
    }

    public static <T> T toObject(Object o, TypeReference<T> toValueTypeRef) {
        return toObject(JsonUtil.stringToJsonObject(JsonUtil.toJson(o)), toValueTypeRef);
    }


    public static <T> T toObject(JsonObject jsonObject, Class<T> tClass) {
        return objectMapper.convertValue(jsonObject, tClass);
    }

    public static <T> T toObject(JsonObject jsonObject, TypeReference<T> toValueTypeRef) {
        return objectMapper.convertValue(jsonObject, toValueTypeRef);
    }

    public static <T> T toObject(JsonArray jsonArray, Class<T> tClass) {
        return objectMapper.convertValue(jsonArray, tClass);
    }

    public static <T> T toObject(JsonArray jsonArray, TypeReference<T> toValueTypeRef) {
        return objectMapper.convertValue(jsonArray, toValueTypeRef);
    }

    public static <T, R extends Iterable> R serializeJson(final T t) {
        try {
            final String content = objectMapper.writeValueAsString(t);
            ;
            return CubeFn.safeDefault(null,
                    () -> CubeFn.getSemi(content.trim().startsWith("{"), null,
                            () -> (R) new JsonObject(content),
                            () -> (R) new JsonArray(content)), content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static java.util.stream.Stream<JsonObject> itJArray(final JsonArray array) {
        return array.stream().filter(item -> item instanceof JsonObject).map(item -> (JsonObject) item);
    }
}
