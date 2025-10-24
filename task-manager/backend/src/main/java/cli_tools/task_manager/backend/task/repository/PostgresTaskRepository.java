package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.backend.filter.EqualFilterCriterion;
import cli_tools.common.backend.filter.FilterCriterion;
import cli_tools.common.backend.filter.GreaterFilterCriterion;
import cli_tools.common.backend.repository.PostgresRepository;
import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.property.FilterPropertySpec;
import cli_tools.common.core.repository.ConstraintViolationException;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.db_schema.tables.records.TaskTagsRecord;
import cli_tools.common.db_schema.tables.records.TasksRecord;
import cli_tools.task_manager.backend.task.Task;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.exception.IntegrityConstraintViolationException;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cli_tools.common.db_schema.Tables.TASKS;
import static cli_tools.common.db_schema.Tables.TASK_TAGS;

@Slf4j
public class PostgresTaskRepository extends PostgresRepository implements TaskRepository {

    private final ObjectReader objectReader;
    private final ObjectWriter objectWriter;

    private final boolean done;

    public PostgresTaskRepository(
            @NonNull DataSource dataSource,
            @NonNull ObjectReader objectReader,
            @NonNull ObjectWriter objectWriter,
            boolean done) {
        super(dataSource);
        this.objectReader = objectReader;
        this.objectWriter = objectWriter;
        this.done = done;
    }

    @Override
    public @NonNull Task create(@NonNull Task task) throws DataAccessException {
        var ctx = ctx();

        Map<String, Object> mutableProperties = new HashMap<>(task.getProperties());
        TasksRecord tasksRecord = TaskRecordMapper.taskToRecord(ctx, mutableProperties, done);
        Object tags = mutableProperties.remove(Task.TAGS);

        JSON mutablePropertiesJson = TaskRecordMapper.propertiesToJson(mutableProperties, objectWriter);

        TaskRecordMapper.putPropertiesToTasksRecord(
                null, mutableProperties, tasksRecord, objectReader, objectWriter);
        tasksRecord.set(TASKS.PROPERTIES, mutablePropertiesJson);

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
        addedTask.getProperties().put(Task.UUID, uuid);
        addedTask.getProperties().put(Task.TAGS, createdTags);
        return addedTask;
    }

    public List<Task> get(List<FilterCriterionInfo> filterCriterionInfos, List<FilterPropertySpec> filterPropertySpecs) throws ServiceException {
        if (filterPropertySpecs == null) {
            return getAll();
        }
        var ctx = ctx();

        var select = ctx.selectFrom(TASKS).where();
        for (FilterPropertySpec filterPropertySpec : filterPropertySpecs) {
            select = filterPropertySpecToWhere(select, (EqualFilterCriterion) FilterCriterion.from(filterPropertySpec));
        }

        return execSelect(ctx, select);
    }

    private SelectConditionStep<TasksRecord> filterPropertySpecToWhere(
            SelectConditionStep<TasksRecord> where, EqualFilterCriterion filterCriterion) {
        if (filterCriterion.getOperand() instanceof String) {
            var field = getStringProperty(filterCriterion.propertyName);
            if (field == null) {
                throw new RuntimeException();
            }
            return where.and(getCondition(field, filterCriterion));
        } else if (filterCriterion.getOperand() instanceof LocalDate) {
            var field = getDateProperty(filterCriterion.propertyName);
            return where.and(getCondition(field, filterCriterion));
        } else {
            throw new RuntimeException();
        }
    }

    private <T> Condition getCondition(TableField<TasksRecord, T> field, FilterCriterion filterCriterion) {
        if (filterCriterion instanceof EqualFilterCriterion criterion) {
            return field.eq((T) criterion.getOperand());
        } else if (filterCriterion instanceof GreaterFilterCriterion criterion) {
            return field.greaterThan((T) criterion.getOperand().getValue());
        } else {
            throw new RuntimeException();
        }
    }

    private TableField<TasksRecord, String> getStringProperty(String propertyName) {
        switch (propertyName) {
            case Task.NAME:
                return TASKS.NAME;
            default:
                throw new RuntimeException();
        }
    }

    private TableField<TasksRecord, LocalDate> getDateProperty(String propertyName) {
        switch (propertyName) {
            case Task.START_DATE:
                return TASKS.START_DATE;
            case Task.DUE_DATE:
                return TASKS.DUE_DATE;
            default:
                throw new RuntimeException();
        }
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

        Task task = TaskRecordMapper.recordToTask(tasksRecord, done);

        Set<UUID> tags = ctx.selectFrom(TASK_TAGS)
                .where(TASK_TAGS.TASK.eq(tasksRecord.getUuid()))
                .stream()
                .map(TaskTagsRecord::getLabel)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!tags.isEmpty()) {
            task.getProperties().put(Task.TAGS, tags);
        }

        TaskRecordMapper.putPropertiesToTask(tasksRecord, task, objectReader);

        return task;
    }

    @Override
    public @NonNull List<Task> getAll() throws DataAccessException {
        var ctx = ctx();
        return execSelect(ctx, ctx.selectFrom(TASKS).where(TASKS.DONE.eq(done)));
    }

    private List<Task> execSelect(DSLContext ctx, SelectConditionStep<TasksRecord> where) {
        List<Task> tasks = new ArrayList<>();

        var tagsByTask = ctx.selectFrom(TASK_TAGS)
                .fetchGroups(TASK_TAGS.TASK, TASK_TAGS.LABEL);

        for (TasksRecord tasksRecord : where) {
            Task task = TaskRecordMapper.recordToTask(tasksRecord, done);
            List<UUID> tags = tagsByTask.get(tasksRecord.getUuid());
            if (tags != null) {
                task.getProperties().put(Task.TAGS, new LinkedHashSet<>(tags));
            }
            if (tasksRecord.getProperties() != null) {
                Map<String, Object> properties = TaskRecordMapper.readProperties(tasksRecord.getProperties(), objectReader);
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

        var mutableProperties = new HashMap<>(task.getProperties());
        TasksRecord tasksRecord = TaskRecordMapper.taskToRecord(ctx, mutableProperties, done);

        var existingPropertiesJson = ctx.selectFrom(TASKS)
                .where(TASKS.UUID.eq(uuid))
                .fetchOne(TASKS.PROPERTIES);

        if (existingPropertiesJson != null) {
            TaskRecordMapper.putPropertiesToTasksRecord(
                    existingPropertiesJson, mutableProperties, tasksRecord, objectReader, objectWriter);
        }

        tasksRecord.attach(ctx.configuration());
        if (tasksRecord.update() != 1) {
            return null;
        }
        tasksRecord.refresh();
        return TaskRecordMapper.recordToTask(tasksRecord, done);
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
        return TaskRecordMapper.recordToTask(tasksRecord, done);
    }

    @Override
    public void deleteAll() {
        ctx().deleteFrom(TASKS)
                .where(TASKS.DONE.eq(done))
                .execute();
    }

}
