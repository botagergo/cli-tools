package task_manager.property;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.*;

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
        return deserialize(jsonNode);
    }

    public Object deserialize(JsonNode jsonNode) {
        if (jsonNode.isTextual()) {
            String str = jsonNode.asText();
            if (str.isEmpty()) {
                return null;
            }
            if (str.charAt(0) == 's') {
                if (str.length() <= 2) {
                    return "";
                } else {
                    return str.substring(2);
                }
            } else if (str.charAt(0) == 'u') {
                return UUID.fromString(str.substring(2));
            }
        } else if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        } else if (jsonNode.isObject()) {
            if (jsonNode.get("type").asText().equals("list")) {
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < jsonNode.get("value").size(); i++) {
                    JsonNode element = jsonNode.get("value").get(i);
                    if (element.isTextual()) {
                        String str = element.asText();
                        if (str.charAt(0) == 's') {
                            if (str.length() <= 2) {
                                list.add("");
                            } else {
                                list.add(str.substring(2));
                            }
                        } else if (str.charAt(0) == 'u') {
                            list.add(UUID.fromString(str.substring(2)));
                        }
                    } else if (element.isBoolean()) {
                        list.add(element.asBoolean());
                    } else {
                        list.add(null);
                    }
                }
                return list;
            } else if (jsonNode.get("type").asText().equals("set")) {
                LinkedHashSet<Object> set = new LinkedHashSet<>();
                for (int i = 0; i < jsonNode.get("value").size(); i++) {
                    JsonNode element = jsonNode.get("value").get(i);
                    if (element.isTextual()) {
                        String str = element.asText();
                        if (str.charAt(0) == 's') {
                            if (str.length() <= 2) {
                                set.add("");
                            } else {
                                set.add(str.substring(2));
                            }
                        } else if (str.charAt(0) == 'u') {
                            set.add(UUID.fromString(str.substring(2)));
                        }
                    } else if (element.isBoolean()) {
                        set.add(element.asBoolean());
                    } else {
                        set.add(null);
                    }
                }
                return set;
            }
        }
        return null;
    }
}