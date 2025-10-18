package cli_tools.common.backend.ordered_label.repository;

import cli_tools.common.core.repository.DataAccessException;
import cli_tools.test_utils.AssertUtils;
import cli_tools.test_utils.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

public class JsonOrderedLabelRepositoryTest {

    private final FileUtils fileUtils = new FileUtils();
    private JsonOrderedLabelRepository repository;

    public JsonOrderedLabelRepositoryTest() throws IOException {}

    @Test
    void test_getAll_notExist() {
        repository = new JsonOrderedLabelRepository(new File("not_exists"));
        assertEquals(repository.getAll("label_type"), List.of());
    }

    private JsonOrderedLabelRepository getRepository(String jsonName, String content, Object... args) throws IOException {
        return new JsonOrderedLabelRepository(fileUtils.makeTempJsonFile(jsonName, content, args));
    }

    private JsonOrderedLabelRepository getRepository(String jsonName) throws IOException {
        return new JsonOrderedLabelRepository(fileUtils.makeTempJsonFile(jsonName));
    }

    @Test
    void test_getAll() throws IOException {
        repository = getRepository("test_getAll", """
                    {
                        "label_type1": { },
                        "label_type2": { "1": "label1", "2": "label2", "3": "label3" }
                    }
                """);
        assertEquals(repository.getAll("label_type1"), List.of());
        assertEquals(repository.getAll("label_type2"), List.of(
                "label1", "label2", "label3"
        ));
        assertEquals(repository.getAll("label_type3"), List.of());
    }

    @Test
    void test_get() throws IOException {
        repository = getRepository("test_get_exists", """
                    {
                        "label_type1": { },
                        "label_type2": { "1": "label1", "-4": "label2", "3": "label3" }
                    }
                """);
        assertEquals(repository.get("label_type2", 1), "label1");
        assertEquals(repository.get("label_type2", -4), "label2");
        assertEquals(repository.get("label_type2", 3), "label3");
        assertNull(repository.get("label_type2", -1));
        assertNull(repository.get("label_type1", 1));
    }

    @Test
    void test_find() throws IOException {
        repository = getRepository("test_find", """
                    {
                        "label_type1": { },
                        "label_type2": { "0": "label1", "-999": "label2", "2": "label3" }
                    }
                """);
        assertEquals(repository.find("label_type2", "label2"), -999);
        assertEquals(repository.find("label_type2", "label3"), 2);
        assertNull(repository.find("label_type1", "label2"));
        assertNull(repository.find("label_type2", "label4"));
    }


    @Test
    void test_emptyFile() throws IOException {
        repository = getRepository("test_emptyFile");
        assertNull(repository.get("label_type", 0));
        assertNull(repository.find("label_type", "label1"));
        assertEquals(repository.getAll("label_type"), List.of());
    }

    @Test
    void test_emptyObj() throws IOException {
        repository = getRepository("test_emptyObj", "{ }");
        assertNull(repository.get("label_type", 0));
        assertNull(repository.find("label_type", "label1"));
        assertEquals(repository.getAll("label_type"), List.of());
    }

    @Test
    void test_invalidFormat_throws() throws IOException {
        repository = getRepository("test_getAll_invalidFormat_throws", """
                    ["label1", "label2", "label3"]
                """);
        assertThrows(DataAccessException.class, () -> repository.getAll("label_type"));
        assertThrows(DataAccessException.class, () -> repository.get("label_type", 123));
        assertThrows(DataAccessException.class, () -> repository.find("label_type", "label1"));
        assertThrows(DataAccessException.class, () -> repository.create("label_type", "label1", 4));
        assertThrows(DataAccessException.class, () -> repository.deleteAll("label_type"));
    }

    @Test
    void test_create_successful() throws IOException {
        repository = getRepository("test_create_successful");
        repository.create("label_type1", "label1", 1);
        repository.create("label_type1", "label2", 3);
        repository.create("label_type1", "label3", -4);
        repository.create("label_type2", "label4", 11);
        repository.create("label_type2", "label5", 999);
        AssertUtils.assertJsonEquals(FileUtils.readFile(repository.getJsonFile()), """
        {
            "label_type1": { "-4": "label3", "1": "label1", "3": "label2" },
            "label_type2": { "11": "label4", "999": "label5" }
        }
        """);
    }

}
