package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.db_schema.tables.Tasks;
import cli_tools.common.db_schema.tables.records.TasksRecord;
import cli_tools.task_manager.backend.task.Task;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Table;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class TaskRecordMapperTest {

    private final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectReader objectReader = objectMapper.readerFor(objectMapper.getTypeFactory().constructType(HashMap.class));
    private final ObjectWriter objectWriter = objectMapper.writerFor(objectMapper.getTypeFactory().constructType(HashMap.class));


    @Mock
    DSLContext ctx;

    public TaskRecordMapperTest() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(ctx.newRecord(ArgumentMatchers.<Table<TasksRecord>>any())).thenAnswer(ignored -> new TasksRecord());
    }

    @BeforeMethod
    void beforeMethod() {
        uuidGenerator.reset();
    }

    @Test
    void test_taskToRecord() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Task.NAME, "task_name");
        properties.put(Task.UUID, uuidGenerator.getUUID());
        properties.put(Task.PARENT, uuidGenerator.getUUID());
        properties.put(Task.STATUS, uuidGenerator.getUUID());
        properties.put(Task.EFFORT, 1);
        properties.put(Task.PRIORITY, 2);
        properties.put(Task.START_DATE, LocalDate.of(2000, 12, 4));
        properties.put(Task.START_TIME, LocalTime.of(18, 33));
        properties.put(Task.DUE_DATE, LocalDate.of(2000, 12, 9));
        properties.put(Task.DUE_TIME, LocalTime.of(13, 33));

        TasksRecord tasksRecord = TaskRecordMapper.taskToRecord(ctx, properties, false);

        assertTrue(tasksRecord.touched(Tasks.TASKS.NAME));
        assertTrue(tasksRecord.touched(Tasks.TASKS.UUID));
        assertTrue(tasksRecord.touched(Tasks.TASKS.PARENT));
        assertTrue(tasksRecord.touched(Tasks.TASKS.STATUS));
        assertTrue(tasksRecord.touched(Tasks.TASKS.EFFORT));
        assertTrue(tasksRecord.touched(Tasks.TASKS.PRIORITY));
        assertTrue(tasksRecord.touched(Tasks.TASKS.START_DATE));
        assertTrue(tasksRecord.touched(Tasks.TASKS.START_TIME));
        assertTrue(tasksRecord.touched(Tasks.TASKS.DUE_DATE));
        assertTrue(tasksRecord.touched(Tasks.TASKS.DUE_TIME));

        assertEquals(tasksRecord.getName(), "task_name");
        assertEquals(tasksRecord.getUuid(), uuidGenerator.getUuids()[0]);
        assertEquals(tasksRecord.getParent(), uuidGenerator.getUuids()[1]);
        assertEquals(tasksRecord.getStatus(), uuidGenerator.getUuids()[2]);
        assertEquals(tasksRecord.getEffort(), 1);
        assertEquals(tasksRecord.getPriority(), 2);
        assertEquals(tasksRecord.getStartDate(), LocalDate.of(2000, 12, 4));
        assertEquals(tasksRecord.getStartTime(), LocalTime.of(18, 33));
        assertEquals(tasksRecord.getDueDate(), LocalDate.of(2000, 12, 9));
        assertEquals(tasksRecord.getDueTime(), LocalTime.of(13, 33));
    }

    @Test
    void test_taskToRecord_notExists() {
        TasksRecord tasksRecord = TaskRecordMapper.taskToRecord(ctx, new HashMap<>(), false);

        assertFalse(tasksRecord.touched(Tasks.TASKS.NAME));
        assertFalse(tasksRecord.touched(Tasks.TASKS.UUID));
        assertFalse(tasksRecord.touched(Tasks.TASKS.PARENT));
        assertFalse(tasksRecord.touched(Tasks.TASKS.STATUS));
        assertFalse(tasksRecord.touched(Tasks.TASKS.EFFORT));
        assertFalse(tasksRecord.touched(Tasks.TASKS.PRIORITY));
        assertFalse(tasksRecord.touched(Tasks.TASKS.START_DATE));
        assertFalse(tasksRecord.touched(Tasks.TASKS.START_TIME));
        assertFalse(tasksRecord.touched(Tasks.TASKS.DUE_DATE));
        assertFalse(tasksRecord.touched(Tasks.TASKS.DUE_TIME));

        assertNull(tasksRecord.getName());
        assertNull(tasksRecord.getUuid());
        assertNull(tasksRecord.getParent());
        assertNull(tasksRecord.getStatus());
        assertNull(tasksRecord.getEffort());
        assertNull(tasksRecord.getPriority());
        assertNull(tasksRecord.getStartDate());
        assertNull(tasksRecord.getStartTime());
        assertNull(tasksRecord.getDueDate());
        assertNull(tasksRecord.getDueTime());
    }

    @Test
    void test_taskToRecord_null() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Task.NAME, null);
        properties.put(Task.UUID, null);
        properties.put(Task.PARENT, null);
        properties.put(Task.STATUS, null);
        properties.put(Task.EFFORT, null);
        properties.put(Task.PRIORITY, null);
        properties.put(Task.START_DATE, null);
        properties.put(Task.START_TIME, null);
        properties.put(Task.DUE_DATE, null);
        properties.put(Task.DUE_TIME, null);

        TasksRecord tasksRecord = TaskRecordMapper.taskToRecord(ctx, properties, false);

        assertTrue(tasksRecord.touched(Tasks.TASKS.NAME));
        assertTrue(tasksRecord.touched(Tasks.TASKS.UUID));
        assertTrue(tasksRecord.touched(Tasks.TASKS.PARENT));
        assertTrue(tasksRecord.touched(Tasks.TASKS.STATUS));
        assertTrue(tasksRecord.touched(Tasks.TASKS.EFFORT));
        assertTrue(tasksRecord.touched(Tasks.TASKS.PRIORITY));
        assertTrue(tasksRecord.touched(Tasks.TASKS.START_DATE));
        assertTrue(tasksRecord.touched(Tasks.TASKS.START_TIME));
        assertTrue(tasksRecord.touched(Tasks.TASKS.DUE_DATE));
        assertTrue(tasksRecord.touched(Tasks.TASKS.DUE_TIME));

        assertNull(tasksRecord.getName());
        assertNull(tasksRecord.getUuid());
        assertNull(tasksRecord.getParent());
        assertNull(tasksRecord.getStatus());
        assertNull(tasksRecord.getEffort());
        assertNull(tasksRecord.getPriority());
        assertNull(tasksRecord.getStartDate());
        assertNull(tasksRecord.getStartTime());
        assertNull(tasksRecord.getDueDate());
        assertNull(tasksRecord.getDueTime());
    }

    @Test
    void test_recordToTask() {
        TasksRecord tasksRecord = new TasksRecord();
        tasksRecord.setName("task_name");
        tasksRecord.setUuid(uuidGenerator.getUUID());
        tasksRecord.setParent(uuidGenerator.getUUID());
        tasksRecord.setStatus(uuidGenerator.getUUID());
        tasksRecord.setEffort(1);
        tasksRecord.setPriority(2);
        tasksRecord.setStartDate(LocalDate.of(2000, 12, 4));
        tasksRecord.setStartTime(LocalTime.of(18, 33));
        tasksRecord.setDueDate(LocalDate.of(2000, 12, 9));
        tasksRecord.setDueTime(LocalTime.of(13, 33));

        Task task = TaskRecordMapper.recordToTask(tasksRecord, false);

        var properties = task.getProperties();
        assertEquals(properties.get(Task.NAME), "task_name");
        assertEquals(properties.get(Task.UUID), uuidGenerator.getUuids()[0]);
        assertEquals(properties.get(Task.PARENT), uuidGenerator.getUuids()[1]);
        assertEquals(properties.get(Task.STATUS), uuidGenerator.getUuids()[2]);
        assertEquals(properties.get(Task.EFFORT), 1);
        assertEquals(properties.get(Task.PRIORITY), 2);
        assertEquals(properties.get(Task.START_DATE), LocalDate.of(2000, 12, 4));
        assertEquals(properties.get(Task.START_TIME), LocalTime.of(18, 33));
        assertEquals(properties.get(Task.DUE_DATE), LocalDate.of(2000, 12, 9));
        assertEquals(properties.get(Task.DUE_TIME), LocalTime.of(13, 33));
    }

    @Test
    void test_recordToTask_notExists() {
        TasksRecord tasksRecord = new TasksRecord();

        Task task = TaskRecordMapper.recordToTask(tasksRecord, false);

        var properties = task.getProperties();
        assertFalse(properties.containsKey(Task.NAME));
        assertFalse(properties.containsKey(Task.UUID));
        assertFalse(properties.containsKey(Task.PARENT));
        assertFalse(properties.containsKey(Task.STATUS));
        assertFalse(properties.containsKey(Task.EFFORT));
        assertFalse(properties.containsKey(Task.PRIORITY));
        assertFalse(properties.containsKey(Task.START_DATE));
        assertFalse(properties.containsKey(Task.START_TIME));
        assertFalse(properties.containsKey(Task.DUE_DATE));
        assertFalse(properties.containsKey(Task.DUE_TIME));
    }

    @Test
    void test_recordToTask_null() {
        TasksRecord tasksRecord = new TasksRecord();
        tasksRecord.setName(null);
        tasksRecord.setUuid(null);
        tasksRecord.setParent(null);
        tasksRecord.setStatus(null);
        tasksRecord.setEffort(null);
        tasksRecord.setPriority(null);
        tasksRecord.setStartDate(null);
        tasksRecord.setStartTime(null);
        tasksRecord.setDueDate(null);
        tasksRecord.setDueTime(null);

        Task task = TaskRecordMapper.recordToTask(tasksRecord, false);

        var properties = task.getProperties();
        assertFalse(properties.containsKey(Task.NAME));
        assertFalse(properties.containsKey(Task.UUID));
        assertFalse(properties.containsKey(Task.PARENT));
        assertFalse(properties.containsKey(Task.STATUS));
        assertFalse(properties.containsKey(Task.EFFORT));
        assertFalse(properties.containsKey(Task.PRIORITY));
        assertFalse(properties.containsKey(Task.START_DATE));
        assertFalse(properties.containsKey(Task.START_TIME));
        assertFalse(properties.containsKey(Task.DUE_DATE));
        assertFalse(properties.containsKey(Task.DUE_TIME));
    }

    @Test
    void test_done() {
        TasksRecord tasksRecord = new TasksRecord();

        Task task = TaskRecordMapper.recordToTask(tasksRecord, false);
        assertFalse(task.isDone());
        task = TaskRecordMapper.recordToTask(tasksRecord, true);
        assertTrue(task.isDone());
    }

    @Test
    void test_putPropertiesToTask() {
        TasksRecord tasksRecord = new TasksRecord();
        tasksRecord.setProperties(JSON.json("""
        {
            "prop1": "task_name",
            "prop2": "3",
            "prop3": "%s"
        }""".formatted(uuidGenerator.getUUID())));
        Task task = new Task();

        TaskRecordMapper.putPropertiesToTask(tasksRecord, task, objectReader);
        var properties = task.getProperties();
        assertEquals(properties.get("prop1"), "task_name");
    }

    @Test
    void test_putPropertiesToTask_invalidJson_throws() {
        TasksRecord tasksRecord = new TasksRecord();
        tasksRecord.setProperties(JSON.json("\"prop1\": \"task_name\""));
        assertThrows(DataAccessException.class, () -> TaskRecordMapper.putPropertiesToTask(tasksRecord, new Task(), objectMapper.readerFor(objectMapper.getTypeFactory().constructType(HashMap.class))));
    }

    @Test
    void test_putPropertiesToTasksRecord() {
        JSON propertiesJson = JSON.json("""
        {
            "prop1": "task_name",
            "prop2": 3,
            "prop3": "%s"
        }""".formatted(uuidGenerator.getUUID()));

        Map<String, Object> properties = new HashMap<>();
        properties.put("prop1", "task_name1");
        properties.put("prop4", "abcd");
        properties.put("prop3", null);

        var tasksRecord = new TasksRecord();

        TaskRecordMapper.putPropertiesToTasksRecord(
                propertiesJson,
                properties,
                tasksRecord,
                objectReader,
                objectWriter);

        propertiesJson = tasksRecord.getProperties();
        properties = TaskRecordMapper.readProperties(propertiesJson, objectReader);
        assertEquals(properties.size(), 3);
        assertEquals(properties.get("prop1"), "task_name1");
        assertEquals(properties.get("prop2"), 3);
        assertEquals(properties.get("prop4"), "abcd");
    }

    @Test
    void test_putPropertiesToTasksRecord_invalidJson_throws() {
        JSON propertiesJson = JSON.json("\"prop1\": \"task_name\"");
        assertThrows(DataAccessException.class, () -> TaskRecordMapper.putPropertiesToTasksRecord(
                propertiesJson,
                Map.of(),
                new TasksRecord(),
                objectReader,
                objectWriter));
    }

}
