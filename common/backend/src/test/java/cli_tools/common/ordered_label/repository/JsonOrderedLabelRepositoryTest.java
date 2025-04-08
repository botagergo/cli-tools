package cli_tools.common.ordered_label.repository;

import cli_tools.common.service.JsonRepositoryCreator;
import org.testng.annotations.Test;
import cli_tools.common.core.data.OrderedLabel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.*;

public class JsonOrderedLabelRepositoryTest {

    public JsonOrderedLabelRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    public void test_getAll_notExist() throws IOException {
        File tempFile = rc.getTempFile("test_getAll_notExist");
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.getAll(), List.of());
    }

    @Test
    public void test_getAll_empty() throws IOException {
        File tempFile = rc.makeTempFile("test_getAll_empty", """
            []
        """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.getAll(), List.of());
    }

    @Test
    public void test_getAll() throws IOException {
        File tempFile = rc.makeTempFile("test_getAll", """
            ["label1", "label2", "label3"]
        """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.getAll(), List.of(
                new OrderedLabel("label1", 0),
                new OrderedLabel("label2", 1),
                new OrderedLabel("label3", 2)
        ));
    }

    @Test
    public void test_get_existing() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
            ["label1", "label2", "label3"]
        """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.get(0), new OrderedLabel("label1", 0));
        assertEquals(repository.get(2), new OrderedLabel("label3", 2));
    }

    @Test
    public void test_get_empty() throws IOException {
        File tempFile = rc.getTempFile("test_get_empty");
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.get(0), null);
    }

    @Test
    public void test_get_notExist() throws IOException {
        File tempFile = rc.makeTempFile("test_get_notExist", """
            ["label1", "label2", "label3"]
        """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.get(-1));
        assertNull(repository.get(3));
    }

    @Test
    public void test_find_existing() throws IOException {
        File tempFile = rc.makeTempFile("test_find_existing", """
            ["label1", "label2", "label3"]
        """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.find("label2"), new OrderedLabel("label2", 1));
        assertEquals(repository.find("label3"), new OrderedLabel("label3", 2));
    }

    @Test
    public void test_find_empty() throws IOException {
        File tempFile = rc.getTempFile("test_find_empty");
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.find("label1"), null);
    }

    @Test
    public void test_find_notExist() throws IOException {
        File tempFile = rc.makeTempFile("test_find_notExist", """
            ["label1", "label2", "label3"]
        """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.find("label4"));
    }

    @Test
    public void test_getAll_invalidFormat_throws() throws IOException {
        File tempFile = rc.makeTempFile("test_getAll_invalidFormat_throws", """
            {"labels":["label1", "label2", "label3"]}
        """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getAll());
    }

    @Test
    public void test_create_successful() throws IOException {
        File tempFile = rc.getTempFile("test_create_successful");
        repository = new JsonOrderedLabelRepository(tempFile);
        repository.create("label1");
        repository.create("label2");
        repository.create("label3");
        assertEquals(repository.getAll(), List.of(
                new OrderedLabel("label1", 0),
                new OrderedLabel("label2", 1),
                new OrderedLabel("label3", 2)));
        assertEquals(Files.readString(tempFile.toPath()).replaceAll("\\s", ""), """
                ["label1","label2","label3"]
        """.replaceAll("\\s", ""));
    }

    private JsonOrderedLabelRepository repository;
    private final JsonRepositoryCreator rc;


}
