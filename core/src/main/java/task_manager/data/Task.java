package task_manager.data;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import lombok.Setter;
import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyManager;
import task_manager.data.property.PropertyOwner;

@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE)
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

    public void setUuidUnchecked(UUID uuid) {
        setPropertyUnchecked("uuid", uuid);
    }

    public UUID getUuid() throws PropertyException {
        return getUuidProperty("uuid");
    }

    public boolean hasUuid() {
        return hasProperty("uuid");
    }

    public void validateUuid() throws PropertyException {
        validateUuidProperty("uuid");
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

    @Override
    @JsonAnyGetter
    public HashMap<String, Object> getRawProperties() {
        return asMap();
    }

    @Override
    @JsonAnySetter
    public PropertyOwner setPropertyUnchecked(String propertyName, Object propertyValue) {
        return super.setPropertyUnchecked(propertyName, propertyValue);
    }

    @Setter
    private static PropertyManager propertyManager;

    private final HashMap<String, Object> properties;

}
