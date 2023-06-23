package task_manager.repository;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;

public class MapSerializer extends StdSerializer<HashMap<String, Object>> {

    public MapSerializer() {
        this(null);
    }

    public MapSerializer(Class<HashMap<String, Object>> t) {
        super(t);
        objectSerializer = new ObjectSerializer();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<HashMap<String, Object>> handledType() {
        return (Class<HashMap<String, Object>>) new HashMap<String, Object>().getClass();
    }

    @Override
    public void serialize(
            HashMap<String, Object> value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();
        for (HashMap.Entry<String, Object> entry : value.entrySet()) {
            jgen.writeFieldName(entry.getKey());
            objectSerializer.serialize(entry.getValue(), jgen, provider);
        }
        jgen.writeEndObject();
    }

    ObjectSerializer objectSerializer;
}