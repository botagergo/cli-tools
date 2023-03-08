package task_manager.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import task_manager.db.property.PropertyException;
import task_manager.db.property.PropertyManager;
import task_manager.db.property.PropertyOwner;

public class Task extends PropertyOwner {

    public Task() {
        this(new HashMap<>());
    }

    private Task(Map<String, Object> taskMap) {
        this.properties = taskMap;
    }

    public void setUuid(UUID uuid) throws PropertyException {
        setProperty("uuid", uuid);
    }

    public UUID getUuid() throws PropertyException {
        return getUuidProperty("uuid");
    }

    public void setName(String name) throws PropertyException {
        setProperty("name", name);
    }

    public String getName() throws PropertyException {
        return getStringProperty("name");
    }

    public void setDone(boolean done) throws PropertyException {
        setProperty("done", done);
    }

    public boolean getDone() throws PropertyException {
        return getBooleanProperty("done");
    }

    public void setTags(List<UUID> tags) throws PropertyException {
        setProperty("tags", tags);
    }

    public List<UUID> getTags() throws PropertyException {
        return getUuidListProperty("tags");
    }

    public Map<String, Object> asMap() {
        return properties;
    }

    public static Task fromMap(Map<String, Object> taskMap) {
        return new Task(taskMap);
    }

    @Override
    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public static void setPropertyManager(PropertyManager propertyManager) {
        Task.propertyManager = propertyManager;
    }

    @Override
    public Map<String, Object> getProperties() {
        return asMap();
    }

    private static PropertyManager propertyManager;
    private Map<String, Object> properties;

}
