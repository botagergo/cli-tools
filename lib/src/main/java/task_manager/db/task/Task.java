package task_manager.db.task;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import task_manager.db.property.PropertyException;
import task_manager.db.property.PropertyManager;
import task_manager.db.property.PropertyOwner;

public class Task extends PropertyOwner {

    public Task() {
        this(new HashMap<>());
    }

    private Task(HashMap<String, Object> taskMap) {
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

    public void setStatus(UUID status) throws PropertyException {
        setProperty("status", status);
    }

    public UUID getStatus() throws PropertyException {
        return getUuidProperty("status");
    }

    public HashMap<String, Object> asMap() {
        return properties;
    }

    public static Task fromMap(HashMap<String, Object> taskMap) {
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
    public HashMap<String, Object> getRawProperties() {
        return asMap();
    }

    private static PropertyManager propertyManager;
    private HashMap<String, Object> properties;

}
