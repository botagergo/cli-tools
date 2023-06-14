package task_manager.property;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.*;

public class ObjectSerializer extends StdSerializer<Object> {

    public ObjectSerializer() {
        this(null);
    }

    public ObjectSerializer(Class<Object> t) {
        super(t);
    }

    @Override
    public Class<Object> handledType() {
        return Object.class;
    }

    @Override
    public void serialize(
            Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        if (value instanceof String str) {
            jgen.writeString("s:" + str);
        } else if (value instanceof UUID uuid) {
            jgen.writeString("u:" + uuid);
        } else if (value instanceof Boolean b) {
            jgen.writeBoolean(b);
        } else if (value instanceof ArrayList<?> list) {
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
        } else if (value instanceof LinkedHashSet<?> set) {
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
            jgen.writeNull();
        }
    }
}