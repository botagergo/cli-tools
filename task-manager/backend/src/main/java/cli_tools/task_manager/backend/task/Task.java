package cli_tools.task_manager.backend.task;

import cli_tools.common.property_lib.PropertyOwner;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Task extends PropertyOwner {

    private final HashMap<String, Object> properties;
    @Getter
    @Setter
    private boolean done;

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

    public String getName() {
        Object name = properties.get(Task.NAME);
        if (name == null) {
            return null;
        }
        if (!(name instanceof String str)) {
            throw new IllegalArgumentException("Property 'name' must be string");
        }
        return str;
    }

    public UUID getStatus() {
        Object status = properties.get(Task.STATUS);
        if (status == null) {
            return null;
        }
        if (!(status instanceof UUID uuid)) {
            throw new IllegalArgumentException("Property 'status' must be UUID");
        }
        return uuid;
    }

    public Integer getEffort() {
        Object effort = properties.get(Task.EFFORT);
        if (effort == null) {
            return null;
        }
        if (!(effort instanceof Integer integer)) {
            throw new IllegalArgumentException("Property 'effort' must be Integer");
        }
        return integer;
    }

    public Integer getPriority() {
        Object priority = properties.get(Task.PRIORITY);
        if (priority == null) {
            return null;
        }
        if (!(priority instanceof Integer integer)) {
            throw new IllegalArgumentException("Property 'priority' must be Integer");
        }
        return integer;
    }

    public Set<UUID> getTags() {
        Object tags = properties.get(Task.TAGS);
        if (tags == null) {
            return null;
        }
        if (!(tags instanceof Set<?> set)) {
            throw new IllegalArgumentException("Property 'tags' must be UUID");
        }
        return (Set<UUID>) set;
    }

    public UUID getParent() {
        Object parent = properties.get(Task.NAME);
        if (parent == null) {
            return null;
        }
        if (!(parent instanceof UUID uuid)) {
            throw new IllegalArgumentException("Property 'parent' must be UUID");
        }
        return uuid;
    }

    public LocalDate getStartDate() {
        Object startDate = properties.get(Task.START_DATE);
        if (startDate == null) {
            return null;
        }
        if (!(startDate instanceof LocalDate localDate)) {
            throw new IllegalArgumentException("Property 'startDate' must be LocalDate");
        }
        return localDate;
    }

    public LocalTime getStartTime() {
        Object startTime = properties.get(Task.START_TIME);
        if (startTime == null) {
            return null;
        }
        if (!(startTime instanceof LocalTime localTime)) {
            throw new IllegalArgumentException("Property 'startTime' must be LocalTime");
        }
        return localTime;
    }

    public LocalDate getDueDate() {
        Object dueDate = properties.get(Task.DUE_DATE);
        if (dueDate == null) {
            return null;
        }
        if (!(dueDate instanceof LocalDate uuid)) {
            throw new IllegalArgumentException("Property 'dueDate' must be LocalDate");
        }
        return uuid;
    }

    public LocalTime getDueTime() {
        Object dueTime = properties.get(Task.DUE_TIME);
        if (dueTime == null) {
            return null;
        }
        if (!(dueTime instanceof LocalTime localTime)) {
            throw new IllegalArgumentException("Property 'dueTime' must be LocalTime");
        }
        return localTime;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Task t && properties.equals(t.properties);
    }

    @Override
    public String toString() {
        return properties.toString();
    }

    public static final String UUID = "uuid";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String EFFORT = "effort";
    public static final String PRIORITY = "priority";
    public static final String TAGS = "tags";
    public static final String PARENT = "parent";
    public static final String START_DATE = "startDate";
    public static final String START_TIME = "startTime";
    public static final String DUE_DATE = "dueDate";
    public static final String DUE_TIME = "dueTime";

}
