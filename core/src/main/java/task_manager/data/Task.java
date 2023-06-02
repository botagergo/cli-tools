package task_manager.data;

import java.util.HashMap;
import java.util.UUID;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import task_manager.property.PropertyOwner;

@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE)
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
    @JsonSetter
    @JsonGetter
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

    private final HashMap<String, Object> properties;

}
