package cli_tools.common.backend;

import cli_tools.common.backend.label.repository.JsonLabelRepository;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.test_utils.AssertUtils;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import cli_tools.test_utils.FileUtils;
import jakarta.inject.Inject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class JsonLabelRepositoryTest {

    private RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final FileUtils fileUtils = new FileUtils();
    @Inject private JsonLabelRepository repository;

    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();

    public JsonLabelRepositoryTest() throws IOException {}

    @BeforeMethod
    void beforeMethod() {
        uuidGenerator.reset();
    }

    @Test
    void test_read_successful() throws IOException {
        repository = getRepository("read_successful", """
                {
                    "mylabel": { "%s": "label1", "%s": "label2" }
                }
                """, uuid1, uuid2);
        assertEquals(repository.getAllWithType("mylabel"), List.of("label1", "label2"));
    }


    @Test
    void test_write_successful() throws IOException {
        repository = getRepository("write_successful.json");
        repository.create("mylabel1", "label1");
        repository.create("mylabel1", "label2");
        repository.create("mylabel2", "label1");
        repository.create("mylabel2", "label4");
        repository.create("mylabel2", "");

        String content = fileUtils.readFile(repository.getJsonFile());
        AssertUtils.assertJsonEquals(content, """
                    {
                        "mylabel1":  { "%s": "label1", "%s": "label2" },
                        "mylabel2": { "%s": "label1", "%s": "label4", "%s": "" }
                    }
                """.formatted(uuidGenerator.getUuids()[0], uuidGenerator.getUuids()[1],
                        uuidGenerator.getUuids()[2], uuidGenerator.getUuids()[3], uuidGenerator.getUuids()[4]));
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

    @Test
    void test_wrongFieldType_throwsException() throws IOException {
        repository = getRepository("wrong_field_type", """
                {
                    "mylabel":"asdf"
                }
                """);
        assertThrows(DataAccessException.class, () -> repository.getData());
    }

    private JsonLabelRepository getRepository(String jsonName, String content, Object... args) throws IOException {
        return new JsonLabelRepository(uuidGenerator, fileUtils.makeTempJsonFile(jsonName, content, args));
    }

    private JsonLabelRepository getRepository(String jsonName) throws IOException {
        return new JsonLabelRepository(uuidGenerator, fileUtils.makeTempJsonFile(jsonName));
    }

}
