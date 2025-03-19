package cli_tools.common.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.NotSerializableException;
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
        } else if (value instanceof Integer i) {
            jgen.writeNumber(i);
        } else if (value instanceof List<?> list) {
            writeObject(jgen, "list", list);
        } else if (value instanceof Set<?> set) {
            writeObject(jgen, "set", set);
        } else if (value == null) {
            jgen.writeNull();
        } else {
            throw new NotSerializableException("Type '" + value.getClass() + " is not serializable");
        }
    }

    private void writeObject(JsonGenerator jgen, String type, Collection<?> collection) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("type", type);
        jgen.writeFieldName("value");
        jgen.writeStartArray();
        for (Object item : collection) {
            if (item instanceof String str) {
                jgen.writeString("s:" + str);
            } else if (item instanceof UUID uuid) {
                jgen.writeString("u:" + uuid);
            } else if (item instanceof Boolean b) {
                jgen.writeBoolean(b);
            } else if (item instanceof Integer i) {
                jgen.writeNumber(i);
            } else if (item == null) {
                jgen.writeNull();
            } else {
                throw new NotSerializableException("Type '" + item.getClass() + " is not serializable");
            }
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}