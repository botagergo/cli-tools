package task_manager;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task_manager.repository.SimpleJsonRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class JsonRepositoryTest {

    @BeforeClass
    public void setupClass() throws IOException {
        tempDir = Files.createTempDirectory("testng");
    }

    private SimpleJsonRepository<ArrayList<Integer>> repository;

    @Test
    public void test_get_successful() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "get_successful", ".json");
        Files.writeString(tempFile, "[1, 2, 3, 4]");
        repository = new SimpleJsonRepositoryImpl(tempFile.toFile());
        assertEquals(repository.getData(), List.of(1, 2, 3, 4));
    }

    @Test
    public void test_write_successful() throws IOException {
        File tempFile = Paths.get(tempDir.toString(), "write_successful").toFile();
        repository = new SimpleJsonRepositoryImpl(tempFile);
        ArrayList<Integer> data = repository.getData();
        data.addAll(List.of(1, 2, 3, 4));
        repository.writeData();
        assertEquals(Files.readString(tempFile.toPath()), "[1,2,3,4]");
    }

    @Test
    public void test_fileNotExists_returnEmpty() throws IOException {
        repository = new SimpleJsonRepositoryImpl(new File("/this/path/does/not/exist"));
        assertEquals(repository.getData().size(), 0);
    }

    @Test
    public void test_fileEmpty_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "empty", ".json");
        repository = new SimpleJsonRepositoryImpl(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_fileContainsNull_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "null", ".json");
        Files.writeString(tempFile, "null");
        repository = new SimpleJsonRepositoryImpl(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_invalidJson_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "invalid", ".json");
        Files.writeString(tempFile, "invalid json content");
        repository = new SimpleJsonRepositoryImpl(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_parentNotExists_parentCreated() throws IOException {
        File jsonFile = Paths.get(tempDir.toString(), "parent_dir/file.json").toFile();
        repository = new SimpleJsonRepositoryImpl(jsonFile);
        repository.writeData();
        assertTrue(jsonFile.exists());
    }
    private Path tempDir;
}
