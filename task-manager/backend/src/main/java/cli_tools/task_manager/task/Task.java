package cli_tools.task_manager.task;

import cli_tools.common.property_lib.PropertyOwner;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class Task extends PropertyOwner {

    public Task() {
        this(new HashMap<>());
    }

    private Task(HashMap<String, Object> taskMap) {
        this.properties = taskMap;
    }

    public static Task fromMap(Map<String, Object> taskMap) {
        return new Task(new HashMap<>(taskMap));
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

    @Getter @Setter
    private boolean done;

}
