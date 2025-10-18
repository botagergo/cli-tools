package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.util.RandomUUIDGenerator;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.util.Utils;
import cli_tools.task_manager.backend.task.Task;
import cli_tools.test_utils.AssertUtils;
import cli_tools.test_utils.FileUtils;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class JsonTaskRepositoryTest {

    private final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private JsonTaskRepository repository;
    private final FileUtils fileUtils = new FileUtils();

    public JsonTaskRepositoryTest() throws IOException {}

    @BeforeMethod
    void beforeMethod() {
        uuidGenerator.reset();
    }

    @Test
    void test_read_successful() throws IOException {
        repository = getRepository("read_successful", """
                [
                    {
                        "name":"s:task1",
                        "values":{
                            "type":"list","value": [true, false]
                        }
                    },
                    {
                        "name":"s:task2",
                        "values":{
                            "type":"set","value": [true, false]
                        }
                    }
                ]
                """);

        List<Task> tasks = repository.getAll();
        assertEquals(tasks.get(0).getProperties().get("name"), "task1");
        assertEquals(tasks.get(0).getProperties().get("values"), Utils.newArrayList(true, false));
        assertEquals(tasks.get(1).getProperties().get("name"), "task2");
        assertEquals(tasks.get(1).getProperties().get("values"), Utils.newLinkedHashSet(true, false));
    }

    private JsonTaskRepository getRepository(String jsonFile, String content, Object... args) throws IOException {
        return new JsonTaskRepository(fileUtils.makeTempJsonFile(jsonFile, content, args), uuidGenerator);
    }

    private JsonTaskRepository getRepository(String jsonFile) throws IOException {
        return new JsonTaskRepository(fileUtils.makeTempJsonFile(jsonFile), uuidGenerator);
    }

    @Test
    void test_write_successful() throws IOException {
        repository = getRepository("write_successful");
        repository.create(Task.fromMap(Utils.newLinkedHashMap("name", "task1",
                "values", Utils.newArrayList(true, false))));
        repository.create(Task.fromMap((Utils.newLinkedHashMap("name", "task2",
                "values", Utils.newLinkedHashSet(false, true)))));
        AssertUtils.assertJsonEquals(FileUtils.readFile(repository.getJsonFile()), """
                [
                    {
                        "name": "s:task1",
                        "uuid": "u:%s",
                        "values":{
                            "type":"list","value": [true, false]
                        }
                    },
                    {
                        "name":"s:task2",
                        "uuid": "u:%s",
                        "values":{
                            "type":"set","value": [false, true]
                        }
                    }
                ]
                """.formatted(uuidGenerator.getUuids()[0], uuidGenerator.getUuids()[1]));
    }

    @Test
    void test_badFormat_throwsException() throws IOException {
        repository = getRepository("bad_format", "[1, 2, 3]");
        assertThrows(DataAccessException.class, () -> repository.getData());

        repository = getRepository("bad_format", "\"some string\"");
        assertThrows(DataAccessException.class, () -> repository.getData());

        repository = getRepository("bad_format", "{\"text\":123}");
        assertThrows(DataAccessException.class, () -> repository.getData());
    }

}
