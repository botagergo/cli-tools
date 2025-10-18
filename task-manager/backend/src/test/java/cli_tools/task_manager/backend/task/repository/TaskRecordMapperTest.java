package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.db_schema.tables.records.TasksRecord;
import cli_tools.task_manager.backend.task.Task;
import org.jooq.DSLContext;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class TaskRecordMapperTest {

    private final TaskRecordMapper taskRecordMapper = new TaskRecordMapper();
    @Mock
    DSLContext ctx;

    @Test
    void test() {
        Map<String, Object> properties = new HashMap<>();
        TasksRecord tasksRecord = taskRecordMapper.taskToRecord(ctx, properties, false);
    }

}
