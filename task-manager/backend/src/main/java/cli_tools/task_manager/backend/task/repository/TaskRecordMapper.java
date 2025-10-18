package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.db_schema.tables.records.TasksRecord;
import cli_tools.task_manager.backend.task.Task;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

import static cli_tools.common.db_schema.Tables.TASKS;

public class TaskRecordMapper {

    public Task recordToTask(TasksRecord taskRecord, boolean done) {
        Task task = new Task();
        Map<String, Object> properties = task.getProperties();
        properties.put("uuid", taskRecord.getUuid());
        if (taskRecord.getName() != null) {
            properties.put("name", taskRecord.getName());
        }
        if (taskRecord.getStatus() != null) {
            properties.put("status", taskRecord.getStatus());
        }
        if (taskRecord.getPriority() != null) {
            properties.put("priority", taskRecord.getPriority());
        }
        if (taskRecord.getEffort() != null) {
            properties.put("effort", taskRecord.getEffort());
        }
        if (taskRecord.getStartDate() != null) {
            properties.put("startDate", taskRecord.getStartDate());
        }
        if (taskRecord.getStartTime() != null) {
            properties.put("startTime", taskRecord.getStartTime());
        }
        if (taskRecord.getEndDate() != null) {
            properties.put("dueDate", taskRecord.getEndDate());
        }
        if (taskRecord.getEndTime() != null) {
            properties.put("dueTime", taskRecord.getEndTime());
        }
        if (taskRecord.getParent() != null) {
            properties.put("parent", taskRecord.getParent());
        }
        task.setDone(done);
        return task;
    }

    public TasksRecord taskToRecord(DSLContext ctx, Map<String, Object> properties, boolean done) {
        TasksRecord tasksRecord = ctx.newRecord(TASKS);

        Object uuid = properties.remove("uuid");
        if (uuid != null) {
            tasksRecord.set(TASKS.UUID, (UUID) uuid);
        }
        Object name = properties.remove("name");
        if (name != null) {
            tasksRecord.set(TASKS.NAME, name.toString());
        }
        Object status = properties.remove("status");
        if (status != null) {
            tasksRecord.set(TASKS.STATUS, (UUID) status);
        }
        Object priority = properties.remove("priority");
        if (priority != null) {
            tasksRecord.set(TASKS.PRIORITY, (Integer) priority);
        }
        Object effort = properties.remove("effort");
        if (effort != null) {
            tasksRecord.set(TASKS.EFFORT, (Integer) effort);
        }
        Object startDate = properties.remove("startDate");
        if (startDate != null) {
            tasksRecord.set(TASKS.START_DATE, (LocalDate) startDate);
        }
        Object startTime = properties.remove("startTime");
        if (startTime != null) {
            tasksRecord.set(TASKS.START_TIME, ((LocalTime) startTime));
        }
        Object endDate = properties.remove("dueDate");
        if (endDate != null) {
            tasksRecord.set(TASKS.END_DATE, (LocalDate) endDate);
        }
        Object endTime = properties.remove("dueTime");
        if (endTime != null) {
            tasksRecord.set(TASKS.END_TIME, (LocalTime) endTime);
        }
        Object parent = properties.remove("parent");
        if (parent != null) {
            tasksRecord.set(TASKS.PARENT, (UUID) parent);
        }

        tasksRecord.setDone(done);

        return tasksRecord;
    }


}
