package task_manager.data;

import java.util.HashMap;
import java.util.UUID;

import task_manager.property.PropertyOwner;

public class Task implements PropertyOwner {

    public Task() {
        this(new HashMap<>());
    }

    private Task(HashMap<String, Object> taskMap) {
        this.properties = taskMap;
    }

    public static Task fromMap(HashMap<String, Object> taskMap) {
        return new Task(taskMap);
    }

    @Override
    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public UUID getUUID() {
        Object uuid = getProperties().get("uuid");
        if (!(uuid instanceof UUID)) {
            return null;
        }
        return (UUID) uuid;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Task t && properties.equals(t.properties);
    }

    private final HashMap<String, Object> properties;

}
