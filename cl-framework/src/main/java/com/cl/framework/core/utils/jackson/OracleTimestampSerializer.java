package com.cl.framework.core.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import oracle.sql.TIMESTAMP;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * OracleTimestampSerializer
 *
 * @author xhz
 */
public class OracleTimestampSerializer extends JsonSerializer<TIMESTAMP> {

    @Override
    public void serialize(TIMESTAMP value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            gen.writeString(value.timestampValue().toString());
        } catch (SQLException e) {
            throw new IOException("Error serializing TIMESTAMP", e);
        }
    }
}
