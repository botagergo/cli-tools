package cli_tools.common.backend.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.NotSerializableException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
        switch (value) {
            case String str -> jgen.writeString("s:" + str);
            case UUID uuid -> jgen.writeString("u:" + uuid);
            case LocalDate localDate -> jgen.writeString("d:" + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            case LocalTime localTime -> jgen.writeString("t:" + localTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
            case Boolean b -> jgen.writeBoolean(b);
            case Integer i -> jgen.writeNumber(i);
            case List<?> list -> writeObject(jgen, "list", list);
            case Set<?> set -> writeObject(jgen, "set", set);
            case null -> jgen.writeNull();
            default -> throw new NotSerializableException("Type '" + value.getClass() + " is not serializable");
        }
    }

    private void writeObject(JsonGenerator jgen, String type, Collection<?> collection) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("type", type);
        jgen.writeFieldName("value");
        jgen.writeStartArray();
        for (Object item : collection) {
            switch (item) {
                case String str -> jgen.writeString("s:" + str);
                case UUID uuid -> jgen.writeString("u:" + uuid);
                case Boolean b -> jgen.writeBoolean(b);
                case Integer i -> jgen.writeNumber(i);
                case null -> jgen.writeNull();
                default -> throw new NotSerializableException("Type '" + item.getClass() + " is not serializable");
            }
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}