package task_manager.core.data;

import task_manager.property_lib.PropertyOwner;

import java.util.HashMap;

public class Task extends PropertyOwner {

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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Task t && properties.equals(t.properties);
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    private final HashMap<String, Object> properties;

}
