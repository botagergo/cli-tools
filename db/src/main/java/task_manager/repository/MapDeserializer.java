package task_manager.repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapDeserializer extends StdDeserializer<HashMap<String, Object>> {

    public MapDeserializer() {
        this(null);
    }

    public MapDeserializer(Class<?> vc) {
        super(vc);
        objectDeserializer = new ObjectDeserializer();
    }

    @Override
    public HashMap<String, Object> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        HashMap<String, Object> properties = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fieldsIter = node.fields();
        while (fieldsIter.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIter.next();
            properties.put(entry.getKey(), objectDeserializer.deserialize(entry.getValue()));
        }
        return properties;
    }

    private final ObjectDeserializer objectDeserializer;
}