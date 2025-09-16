package cli_tools.common.backend.repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapDeserializer extends StdDeserializer<HashMap<String, Object>> {

    private final ObjectDeserializer objectDeserializer;

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
        if (!node.isObject()) {
            throw new IOException("Expected JSON object");
        }

        HashMap<String, Object> properties = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fieldsIter = node.fields();
        while (fieldsIter.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIter.next();
            properties.put(entry.getKey(), objectDeserializer.deserialize(jp, entry.getValue()));
        }
        return properties;
    }
}