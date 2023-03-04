package task_manager.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Task {

    public Task() {
        this(new HashMap<>());
    }

    private Task(Map<String, Object> taskMap) {
        this.properties = taskMap;
    }

    public void setUuid(UUID uuid) {
        properties.put("uuid", uuid);
    }

    public UUID getUuid() {
        return (UUID) properties.get("uuid");
    }

    public void setName(String name) {
        properties.put("name", name);
    }

    public String getName() {
        return (String) properties.get("name");
    }

    public void setDone(boolean done) {
        properties.put("done", done);
    }

    public boolean getDone() {
        return (properties.containsKey("done") && (boolean) properties.get("done") == true);
    }

    public void setTags(List<UUID> tags) {
        properties.put("tags", tags);
    }

    public List<UUID> getTags() {
        if (properties.containsKey("tags")) {
            return ((List<String>) properties.get("tags")).stream().map(
                    tagUuidStr -> UUID.fromString(tagUuidStr)).collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    public Map<String, Object> asMap() {
        return properties;
    }

    public static Task fromMap(Map<String, Object> taskMap) {
        return new Task(taskMap);
    }

    private Map<String, Object> properties;

}
