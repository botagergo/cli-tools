package task_manager.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.*;

public class MapSerializer extends StdSerializer<HashMap<String, Object>> {

    public MapSerializer() {
        this(null);
    }

    public MapSerializer(Class<HashMap<String, Object>> t) {
        super(t);
    }

    @Override
    public Class<HashMap<String, Object>> handledType() {
        return (Class<HashMap<String, Object>>) new HashMap<String, Object>().getClass();
    }

    @Override
    public void serialize(
            HashMap<String, Object> value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();

        for (HashMap.Entry<String, Object> entry : value.entrySet()) {
            if (entry.getValue() instanceof String str) {
                jgen.writeStringField(entry.getKey(), "s:" + str);
            } else if (entry.getValue() instanceof UUID uuid) {
                jgen.writeStringField(entry.getKey(), "u:" + uuid);
            } else if (entry.getValue() instanceof Boolean b) {
                jgen.writeBooleanField(entry.getKey(), b);
            } else if (entry.getValue() instanceof List<?> list) {
                jgen.writeFieldName(entry.getKey());
                jgen.writeStartObject();
                jgen.writeStringField("type", "list");
                jgen.writeFieldName("value");
                jgen.writeStartArray();
                for (Object item : list) {
                    if (item instanceof String str) {
                        jgen.writeString("s:" + str);
                    } else if (item instanceof UUID uuid) {
                        jgen.writeString("u:" + uuid);
                    } else if (item instanceof Boolean b) {
                        jgen.writeBoolean(b);
                    } else {
                        jgen.writeNull();
                    }
                }
                jgen.writeEndArray();
                jgen.writeEndObject();
            } else if (entry.getValue() instanceof Set<?> set) {
                jgen.writeFieldName(entry.getKey());
                jgen.writeStartObject();
                jgen.writeStringField("type", "set");
                jgen.writeFieldName("value");
                jgen.writeStartArray();
                for (Object item : set) {
                    if (item instanceof String str) {
                        jgen.writeString("s:" + str);
                    } else if (item instanceof UUID uuid) {
                        jgen.writeString("u:" + uuid);
                    } else if (item instanceof Boolean b) {
                        jgen.writeBoolean(b);
                    } else {
                        jgen.writeNull();
                    }
                }
                jgen.writeEndArray();
                jgen.writeEndObject();
            } else {
                jgen.writeNullField(entry.getKey());
            }
        }

        jgen.writeEndObject();
    }
}