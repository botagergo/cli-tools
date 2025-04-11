package cli_tools.common.service;

import cli_tools.common.repository.SimpleJsonRepository;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class JsonRepositoryTest {

    private final JsonRepositoryCreator rc;
    private SimpleJsonRepository<ArrayList<Integer>> repository;

    public JsonRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    public void test_get_successful() throws IOException {
        repository = rc.createRepository("get_successful", "[1, 2, 3, 4]");
        assertEquals(repository.getData(), List.of(1, 2, 3, 4));
    }

    @Test
    public void test_write_successful() throws IOException {
        File tempFile = rc.getTempFile("write_successful");
        repository = rc.createRepository(tempFile);
        ArrayList<Integer> data = repository.getData();
        data.addAll(List.of(1, 2, 3, 4));
        repository.writeData();
        assertEquals(Files.readString(tempFile.toPath()).replaceAll("\\s", ""), "[1,2,3,4]");
    }

    @Test
    public void test_fileNotExists_returnEmpty() throws IOException {
        repository = rc.createRepository(new File("/this/path/does/not/exist"));
        assertEquals(repository.getData().size(), 0);
    }

    @Test
    public void test_fileEmpty_throwsException() throws IOException {
        repository = rc.createRepository("empty", null);
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_fileContainsNull_throwsException() throws IOException {
        repository = rc.createRepository("null", "null");
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_invalidJson_throwsException() throws IOException {
        repository = rc.createRepository("invalid", "invalid json content");
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_parentNotExists_parentCreated() throws IOException {
        File jsonFile = rc.getTempFile("parent_dir/file.json");
        repository = rc.createRepository(jsonFile);
        repository.writeData();
        assertTrue(jsonFile.exists());
    }

}
