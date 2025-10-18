package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.backend.repository.PostgresRepository;
import cli_tools.common.core.repository.ConstraintViolationException;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.db_schema.tables.records.TaskTagsRecord;
import cli_tools.common.db_schema.tables.records.TasksRecord;
import cli_tools.task_manager.backend.task.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jooq.JSON;
import org.jooq.exception.IntegrityConstraintViolationException;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

import static cli_tools.common.db_schema.Tables.TASKS;
import static cli_tools.common.db_schema.Tables.TASK_TAGS;

@Slf4j
public class PostgresTaskRepository extends PostgresRepository implements TaskRepository {

    private final ObjectReader objectReader;
    private final ObjectWriter objectWriter;

    private final TaskRecordMapper taskRecordMapper;

    private final boolean done;

    public PostgresTaskRepository(
            @NonNull DataSource dataSource,
            @NonNull ObjectReader objectReader,
            @NonNull ObjectWriter objectWriter,
            boolean done) {
        super(dataSource);
        this.objectReader = objectReader;
        this.objectWriter = objectWriter;
        this.taskRecordMapper = new TaskRecordMapper();
        this.done = done;
    }

    @Override
    public @NonNull Task create(@NonNull Task task) throws DataAccessException {
        var ctx = ctx();

        Map<String, Object> mutableProperties = new HashMap<>(task.getProperties());
        TasksRecord tasksRecord = taskRecordMapper.taskToRecord(ctx, mutableProperties, done);
        Object tags = mutableProperties.remove("tags");

        String mutablePropertiesJson;
        try {
            mutablePropertiesJson = objectWriter.writeValueAsString(mutableProperties);
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Error converting task properties to json", e);
        }
        tasksRecord.set(TASKS.PROPERTIES, JSON.json(mutablePropertiesJson));

        try {
            tasksRecord = ctx.insertInto(TASKS)
                    .set(tasksRecord)
                    .returning(TASKS.UUID)
                    .fetchSingle();
        } catch (IntegrityConstraintViolationException e) {
            throw new ConstraintViolationException(e.getMessage(), e);
        }

        UUID uuid = tasksRecord.getUuid();

        Collection<UUID> createdTags = new LinkedHashSet<>();
        if (tags != null) {
            var insert = ctx.insertInto(TASK_TAGS, TASK_TAGS.TASK, TASK_TAGS.LABEL);
            for (Object tag : (Collection<?>) tags) {
                insert = insert.values(uuid, (UUID) tag);
            }
            createdTags = insert.returning(TASK_TAGS.LABEL).fetch().getValues(TASK_TAGS.LABEL);
        }

        Task addedTask = new Task();
        addedTask.getProperties().putAll(task.getProperties());
        addedTask.getProperties().put("uuid", uuid);
        addedTask.getProperties().put("tags", createdTags);
        return addedTask;
    }

    @Override
    public Task get(@NonNull UUID uuid) throws DataAccessException {
        var ctx = ctx();

        var tasksRecord = ctx.selectFrom(TASKS)
                .where(TASKS.UUID.eq(uuid))
                .and(TASKS.DONE.eq(done))
                .fetchOne();
        if (tasksRecord == null) {
            return null;
        }

        Task task = taskRecordMapper.recordToTask(tasksRecord, done);

        Set<UUID> tags = ctx.selectFrom(TASK_TAGS)
                .where(TASK_TAGS.TASK.eq(tasksRecord.getUuid()))
                .stream()
                .map(TaskTagsRecord::getLabel)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!tags.isEmpty()) {
            task.getProperties().put("tags", tags);
        }

        if (tasksRecord.getProperties() != null) {
            Map<String, Object> properties = readProperties(tasksRecord.getProperties());
            if (!properties.isEmpty()) {
                task.getProperties().putAll(properties);
            }
        }

        return task;
    }

    @Override
    public @NonNull List<Task> getAll() throws DataAccessException {
        var ctx = ctx();

        List<Task> tasks = new ArrayList<>();

        var tagsByTask = ctx.selectFrom(TASK_TAGS)
                .fetchGroups(TASK_TAGS.TASK, TASK_TAGS.LABEL);
        for (TasksRecord tasksRecord : ctx.selectFrom(TASKS).where(TASKS.DONE.eq(done))) {
            Task task = taskRecordMapper.recordToTask(tasksRecord, done);
            List<UUID> tags = tagsByTask.get(tasksRecord.getUuid());
            if (tags != null) {
                task.getProperties().put("tags", new LinkedHashSet<>(tags));
            }
            if (tasksRecord.getProperties() != null) {
                Map<String, Object> properties;
                try {
                    properties = objectReader.readValue(tasksRecord.getProperties().data());
                } catch (JsonProcessingException e) {
                    throw new DataAccessException("Error reading properties from JSON: %s"
                            .formatted(tasksRecord.getProperties().data().replace(System.lineSeparator(), " ")), e);
                }
                task.getProperties().putAll(properties);
            }
            tasks.add(task);
        }
        return tasks;
    }

    @Override
    public Task update(@NonNull Task task) {
        UUID uuid = task.getUUID();
        if (uuid == null) {
            throw new IllegalArgumentException("Missing task UUID");
        }

        var ctx = ctx();

        var existingPropertiesJson = ctx.selectFrom(TASKS)
                .where(TASKS.UUID.eq(uuid))
                .fetchOne(TASKS.PROPERTIES);
        if (existingPropertiesJson == null) {
            return null;
        }

        Map<String, Object> mergedProperties = readProperties(existingPropertiesJson);
        mergedProperties.putAll(task.getProperties());

        TasksRecord tasksRecord = taskRecordMapper.taskToRecord(ctx, mergedProperties, done);
        tasksRecord.attach(ctx.configuration());
        if (tasksRecord.update() != 1) {
            return null;
        }
        tasksRecord.refresh();
        return taskRecordMapper.recordToTask(tasksRecord, done);
    }

    @Override
    public Task delete(@NonNull UUID uuid) {
        TasksRecord tasksRecord = ctx().deleteFrom(TASKS)
                .where(TASKS.UUID.eq(uuid))
                .and(TASKS.DONE.eq(done))
                .returning().fetchOne();
        if (tasksRecord == null) {
            return null;
        }
        return taskRecordMapper.recordToTask(tasksRecord, done);
    }

    @Override
    public void deleteAll() {
        ctx().deleteFrom(TASKS)
                .where(TASKS.DONE.eq(done))
                .execute();
    }

    private Map<String, Object> readProperties(JSON propertiesJson) {
        try {
            return objectReader.readValue(propertiesJson.data());
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Error reading properties from JSON: %s"
                    .formatted(propertiesJson.data().replace(System.lineSeparator(), "")), e);
        }
    }
}
