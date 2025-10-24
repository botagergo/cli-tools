package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.db_schema.tables.records.TasksRecord;
import cli_tools.task_manager.backend.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jooq.DSLContext;
import org.jooq.JSON;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

import static cli_tools.common.db_schema.Tables.TASKS;

public class TaskRecordMapper {

    public static Task recordToTask(TasksRecord taskRecord, boolean done) {
        Task task = new Task();
        Map<String, Object> properties = task.getProperties();
        if (taskRecord.getUuid() != null)
            properties.put(Task.UUID, taskRecord.getUuid());
        if (taskRecord.getName() != null)
            properties.put(Task.NAME, taskRecord.getName());
        if (taskRecord.getStatus() != null)
            properties.put(Task.STATUS, taskRecord.getStatus());
        if (taskRecord.getPriority() != null)
            properties.put(Task.PRIORITY, taskRecord.getPriority());
        if (taskRecord.getEffort() != null)
            properties.put(Task.EFFORT, taskRecord.getEffort());
        if (taskRecord.getStartDate() != null)
            properties.put(Task.START_DATE, taskRecord.getStartDate());
        if (taskRecord.getStartTime() != null)
            properties.put(Task.START_TIME, taskRecord.getStartTime());
        if (taskRecord.getDueDate() != null)
            properties.put(Task.DUE_DATE, taskRecord.getDueDate());
        if (taskRecord.getDueTime() != null)
            properties.put(Task.DUE_TIME, taskRecord.getDueTime());
        if (taskRecord.getParent() != null)
            properties.put(Task.PARENT, taskRecord.getParent());
        task.setDone(done);
        return task;
    }

    public static TasksRecord taskToRecord(DSLContext ctx, Map<String, Object> properties, boolean done) {
        TasksRecord tasksRecord = ctx.newRecord(TASKS);
        if (properties.containsKey(Task.UUID))
            tasksRecord.set(TASKS.UUID, (UUID) properties.get(Task.UUID));
        if (properties.containsKey(Task.NAME))
            tasksRecord.set(TASKS.NAME, (String) properties.get(Task.NAME));
        if (properties.containsKey(Task.STATUS))
            tasksRecord.set(TASKS.STATUS, (UUID) properties.get(Task.STATUS));
        if (properties.containsKey(Task.PRIORITY))
            tasksRecord.set(TASKS.PRIORITY, (Integer) properties.get(Task.PRIORITY));
        if (properties.containsKey(Task.EFFORT))
            tasksRecord.set(TASKS.EFFORT, (Integer) properties.get(Task.EFFORT));
        if (properties.containsKey(Task.START_DATE))
            tasksRecord.set(TASKS.START_DATE, (LocalDate) properties.get(Task.START_DATE));
        if (properties.containsKey(Task.START_TIME))
            tasksRecord.set(TASKS.START_TIME, (LocalTime) properties.get(Task.START_TIME));
        if (properties.containsKey(Task.DUE_DATE))
            tasksRecord.set(TASKS.DUE_DATE, (LocalDate) properties.get(Task.DUE_DATE));
        if (properties.containsKey(Task.DUE_TIME))
            tasksRecord.set(TASKS.DUE_TIME, (LocalTime) properties.get(Task.DUE_TIME));
        if (properties.containsKey(Task.PARENT))
            tasksRecord.set(TASKS.PARENT, (UUID) properties.get(Task.PARENT));
        tasksRecord.setDone(done);
        return tasksRecord;
    }

    public static void putPropertiesToTask(TasksRecord tasksRecord, Task task, ObjectReader objectReader) {
        if (tasksRecord.getProperties() != null) {
            Map<String, Object> properties = readProperties(tasksRecord.getProperties(), objectReader);
            if (!properties.isEmpty()) {
                task.getProperties().putAll(properties);
            }
        }
    }

    public static  void putPropertiesToTasksRecord(
            JSON existingPropertiesJson,
            Map<String, Object> properties,
            TasksRecord tasksRecord,
            ObjectReader objectReader,
            ObjectWriter objectWriter) {
        Map<String, Object> mergedProperties;
        if (existingPropertiesJson != null) {
            mergedProperties = readProperties(existingPropertiesJson, objectReader);
            for (var entry : properties.entrySet()) {
                if (entry.getValue() == null) {
                    mergedProperties.remove(entry.getKey());
                } else {
                    mergedProperties.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            mergedProperties = properties;
        }
        tasksRecord.setProperties(propertiesToJson(mergedProperties, objectWriter));
    }

    public static Map<String, Object> readProperties(JSON propertiesJson, ObjectReader objectReader) {
        try {
            return objectReader.readValue(propertiesJson.data());
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Error reading properties from JSON: %s"
                    .formatted(propertiesJson.data().replace(System.lineSeparator(), "")), e);
        }
    }

    public static JSON propertiesToJson(Map<String, Object> properties, ObjectWriter objectWriter) {
        try {
            return JSON.json(objectWriter.writeValueAsString(properties));
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Error converting task properties to json", e);
        }
    }


}
