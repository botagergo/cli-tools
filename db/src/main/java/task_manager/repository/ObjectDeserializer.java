package task_manager.repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;

public class ObjectDeserializer extends StdDeserializer<Object> {

    public ObjectDeserializer() {
        this(null);
    }

    public ObjectDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        return deserialize(jp, jsonNode);
    }

    public Object deserialize(JsonParser jp, JsonNode jsonNode) throws JsonMappingException {
        if (jsonNode.isTextual()) {
            return deserializeString(jsonNode);
        } else if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        } else if (jsonNode.isInt()) {
            return jsonNode.asInt();
        } else if (jsonNode.isObject()) {
            JsonNode typeNode = jsonNode.get("type");
            if (typeNode == null) {
                throw JsonMappingException.from(jp, "Missing 'type' field for collection");
            }

            String collectionType = typeNode.asText();
            if (collectionType.equals("list")) {
                ArrayList<Object> list = new ArrayList<>();
                deserializeCollection(jp, jsonNode, list);
                return list;
            } else if (collectionType.equals("set")) {
                LinkedHashSet<Object> set = new LinkedHashSet<>();
                deserializeCollection(jp, jsonNode, set);
                return set;
            } else {
                throw JsonMappingException.from(jp, "Invalid collection type: " + collectionType);
            }
        } else if (jsonNode.isNull()) {
            return null;
        } else {
            throw JsonMappingException.from(jp, "Value cannot be deserialized: " + jsonNode.toPrettyString());
        }
    }

    private Object deserializeString(JsonNode jsonNode) {
        String str = jsonNode.asText();
        if (str.startsWith("s:")) {
            return str.substring(2);
        } else if (str.startsWith("u:")) {
            return UUID.fromString(str.substring(2));
        } else {
            return str;
        }
    }

    private void deserializeCollection(JsonParser jp, JsonNode jsonNode, Collection<Object> collection) throws JsonMappingException {
        JsonNode value = jsonNode.get("value");
        if (value == null) {
            throw JsonMappingException.from(jp, "Missing 'value' field for collection");
        }

        for (int i = 0; i < value.size(); i++) {
            JsonNode element = value.get(i);
            if (element.isTextual()) {
                collection.add(deserializeString(element));
            } else if (element.isBoolean()) {
                collection.add(element.asBoolean());
            } else if (element.isInt()) {
                collection.add(element.asInt());
            } else if (element.isNull()) {
                collection.add(null);
            } else {
                throw JsonMappingException.from(jp, "Value cannot be deserialized: " + jsonNode.toPrettyString());
            }
        }
    }
}