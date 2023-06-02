package task_manager.repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.*;

public class MapDeserializer extends StdDeserializer<HashMap<String, Object>> {

    public MapDeserializer() {
        this(null);
    }

    public MapDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public HashMap<String, Object> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        HashMap<String, Object> properties = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fieldsIter = node.fields();
        while (fieldsIter.hasNext()) {
            Map.Entry<String, JsonNode> entry = fieldsIter.next();
            if (entry.getValue().isTextual()) {
                String str = entry.getValue().asText();
                if (str.charAt(0) == 's') {
                    if (str.length() <= 2) {
                        properties.put(entry.getKey(), "");
                    } else {
                        properties.put(entry.getKey(), str.substring(2));
                    }
                } else if (str.charAt(0) == 'u') {
                    properties.put(entry.getKey(), UUID.fromString(str.substring(2)));
                }
            } else if (entry.getValue().isBoolean()) {
                properties.put(entry.getKey(), entry.getValue().asBoolean());
            } else if (entry.getValue().isObject()) {
                if (entry.getValue().get("type").asText().equals("list")) {
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < entry.getValue().get("value").size(); i++) {
                        JsonNode element = entry.getValue().get("value").get(i);
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
                            list.add(entry.getValue().asBoolean());
                        } else {
                            list.add(null);
                        }
                    }
                    properties.put(entry.getKey(), list);
                } else if (entry.getValue().get("type").asText().equals("set")) {
                    LinkedHashSet<Object> set = new LinkedHashSet<>();
                    for (int i = 0; i < entry.getValue().get("value").size(); i++) {
                        JsonNode element = entry.getValue().get("value").get(i);
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
                            set.add(entry.getValue().asBoolean());
                        } else {
                            set.add(null);
                        }
                    }
                    properties.put(entry.getKey(), set);
                }
            } else {
                properties.put(entry.getKey(), null);
            }
        }
        return properties;
    }
}