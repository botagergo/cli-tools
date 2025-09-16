package cli_tools.common.backend.ordered_label.repository;

import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.backend.service.JsonRepositoryCreator;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.*;

public class JsonOrderedLabelRepositoryTest {

    private final JsonRepositoryCreator rc;
    private JsonOrderedLabelRepository repository;

    public JsonOrderedLabelRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    void test_getAll_notExist() throws IOException {
        File tempFile = rc.getTempFile("test_getAll_notExist");
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.getAll("label_type"), List.of());
    }

    @Test
    void test_getAll_empty() throws IOException {
        File tempFile = rc.makeTempFile("test_getAll_empty", """
                    {
                        "label_type1": [],
                        "label_type2": ["label1", "label2", "label3"]
                    }
                """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.getAll("label_type1"), List.of());
    }

    @Test
    void test_getAll() throws IOException {
        File tempFile = rc.makeTempFile("test_getAll", """
                    {
                        "label_type1": [],
                        "label_type2": ["label1", "label2", "label3"]
                    }
                """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.getAll("label_type2"), List.of(
                "label1", "label2", "label3"
        ));
    }

    @Test
    void test_get_existing() throws IOException {
        File tempFile = rc.makeTempFile("test_get_existing", """
                    {
                        "label_type1": [],
                        "label_type2": ["label1", "label2", "label3"]
                    }
                """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.get("label_type2", 0), "label1");
        assertEquals(repository.get("label_type2", 2), "label3");
    }

    @Test
    void test_get_empty() throws IOException {
        File tempFile = rc.getTempFile("test_get_empty");
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.get("label_type", 0), null);
    }

    @Test
    void test_get_emptyObj() throws IOException {
        File tempFile = rc.makeTempFile("test_get_empty", "{}");
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.get("label_type", 0), null);
    }

    @Test
    void test_get_notExist() throws IOException {
        File tempFile = rc.makeTempFile("test_get_notExist", """
                    {
                        "label_type1": [],
                        "label_type2": ["label1", "label2", "label3"]
                    }
                """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.get("label_type2", -1));
        assertNull(repository.get("label_type2", 3));
    }

    @Test
    void test_find_existing() throws IOException {
        File tempFile = rc.makeTempFile("test_find_existing", """
                    {
                        "label_type1": [],
                        "label_type2": ["label1", "label2", "label3"]
                    }
                """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertEquals(repository.find("label_type2", "label2"), 1);
        assertEquals(repository.find("label_type2", "label3"), 2);
    }

    @Test
    void test_find_empty() throws IOException {
        File tempFile = rc.getTempFile("test_find_empty");
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.find("label_type", "label1"), null);
    }

    @Test
    void test_find_notExist() throws IOException {
        File tempFile = rc.makeTempFile("test_find_notExist", """
                    {
                        "label_type1": [],
                        "label_type2": ["label1", "label2", "label3"]
                    }
                """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertNull(repository.find("label_type2", "label4"));
    }

    @Test
    void test_getAll_invalidFormat_throws() throws IOException {
        File tempFile = rc.makeTempFile("test_getAll_invalidFormat_throws", """
                    ["label1", "label2", "label3"]
                """);
        repository = new JsonOrderedLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getAll("label_type"));
    }

    @Test
    void test_create_successful() throws IOException {
        File tempFile = rc.getTempFile("test_create_successful");
        repository = new JsonOrderedLabelRepository(tempFile);
        repository.create("label_type1", "label1");
        repository.create("label_type1", "label2");
        repository.create("label_type1", "label3");
        repository.create("label_type2", "label4");
        repository.create("label_type2", "label5");
        assertEquals(repository.getAll("label_type1"), List.of(
                "label1", "label2", "label3"));
        assertEquals(repository.getAll("label_type2"), List.of(
                "label4", "label5"));
        assertEquals(Files.readString(tempFile.toPath()).replaceAll("\\s", ""), """
                        {
                            "label_type1": ["label1","label2","label3"],
                            "label_type2": ["label4","label5"]
                        }
                """.replaceAll("\\s", ""));
    }


}
