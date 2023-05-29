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
    @JsonAnyGetter
    public HashMap<String, Object> getRawProperties() {
        return properties;
    }

    public UUID getUUID() {
        Object uuid = getRawProperties().get("uuid");
        if (!(uuid instanceof String)) {
            return null;
        }

        try {
            return UUID.fromString((String) uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private final HashMap<String, Object> properties;

}
