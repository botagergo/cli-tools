package task_manager;

import com.beust.jcommander.internal.Lists;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task_manager.data.Task;
import task_manager.repository.task.JsonTaskRepository;
import task_manager.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class JsonTaskRepositoryTest {

    @BeforeClass
    public void setupClass() throws IOException {
        tempDir = Files.createTempDirectory("testng");
    }

    @Test
    public void test_read_successful() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "read_successful", ".json");
        Files.writeString(tempFile, "[{\"properties\":{\"name\":\"s:task1\",\"values\":{\"type\":\"list\",\"value\":[true,false]}}},{\"properties\":{\"name\":\"s:task2\",\"values\":{\"type\":\"set\",\"value\":[true,false]}}}]");
        repository = new JsonTaskRepository(tempFile.toFile());

        List<Task> tasks = repository.getAll();
        assertEquals(tasks.get(0).getProperties().get("name"), "task1");
        assertEquals(tasks.get(0).getProperties().get("values"), Lists.newArrayList(true, false));
        assertEquals(tasks.get(1).getProperties().get("name"), "task2");
        assertEquals(tasks.get(1).getProperties().get("values"), Utils.newLinkedHashSet(true, false));
    }

    @Test
    public void test_write_successful() throws IOException {
        File tempFile = Paths.get(tempDir.toString(), "write_successful.json").toFile();
        repository = new JsonTaskRepository(tempFile);
        repository.create(Task.fromMap(Utils.newLinkedHashMap("name", "task1",
                "values", Lists.newArrayList(true, false))));
        repository.create(Task.fromMap((Utils.newLinkedHashMap("name", "task2",
                "values", Utils.newLinkedHashSet(false, true)))));
        String content = Files.readString(tempFile.toPath());
        assertEquals(content, "[{\"properties\":{\"name\":\"s:task1\",\"values\":{\"type\":\"list\",\"value\":[true,false]}}},{\"properties\":{\"name\":\"s:task2\",\"values\":{\"type\":\"set\",\"value\":[false,true]}}}]");
    }

    @Test
    public void test_badFormat_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "bad_format", ".json");

        Files.writeString(tempFile, "[1, 2, 3]");
        repository = new JsonTaskRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());

        Files.writeString(tempFile, "\"some string\"");
        repository = new JsonTaskRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());

        Files.writeString(tempFile, "{\"name\":123}");
        repository = new JsonTaskRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());
    }

    private JsonTaskRepository repository;
    private Path tempDir;

}
