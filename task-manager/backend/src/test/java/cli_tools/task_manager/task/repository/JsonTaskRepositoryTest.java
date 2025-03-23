package cli_tools.task_manager.task.repository;

import org.testng.annotations.Test;
import cli_tools.task_manager.task.Task;
import cli_tools.common.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class JsonTaskRepositoryTest {

    public JsonTaskRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    public void test_read_successful() throws IOException {
        repository = new JsonTaskRepository(rc.makeTempFile("read_successful", """
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
                """));

        List<Task> tasks = repository.getAll();
        assertEquals(tasks.get(0).getProperties().get("name"), "task1");
        assertEquals(tasks.get(0).getProperties().get("values"), Utils.newArrayList(true, false));
        assertEquals(tasks.get(1).getProperties().get("name"), "task2");
        assertEquals(tasks.get(1).getProperties().get("values"), Utils.newLinkedHashSet(true, false));
    }

    @Test
    public void test_write_successful() throws IOException {
        File tempFile = rc.getTempFile("write_successful");
        repository = new JsonTaskRepository(tempFile);
        repository.create(Task.fromMap(Utils.newLinkedHashMap("name", "task1",
                "values", Utils.newArrayList(true, false))));
        repository.create(Task.fromMap((Utils.newLinkedHashMap("name", "task2",
                "values", Utils.newLinkedHashSet(false, true)))));
        String content = Files.readString(tempFile.toPath());
        assertEquals(content.replaceAll("\\s", ""), """
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
                            "type":"set","value": [false, true]
                        }
                    }
                ]
                """.replaceAll("\\s", ""));
    }

    @Test
    public void test_badFormat_throwsException() throws IOException {
        File tempFile = rc.getTempFile("bad_format");

        Files.writeString(tempFile.toPath(), "[1, 2, 3]");
        repository = new JsonTaskRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());

        Files.writeString(tempFile.toPath(), "\"some string\"");
        repository = new JsonTaskRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());

        Files.writeString(tempFile.toPath(), "{\"text\":123}");
        repository = new JsonTaskRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());
    }

    private JsonTaskRepository repository;
    private final JsonRepositoryCreator rc;

}
