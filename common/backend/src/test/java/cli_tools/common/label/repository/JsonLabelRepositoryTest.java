package cli_tools.common.label.repository;

import cli_tools.common.core.data.Label;
import cli_tools.common.service.JsonRepositoryCreator;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class JsonLabelRepositoryTest {

    final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final JsonRepositoryCreator rc;
    private JsonLabelRepository repository;

    public JsonLabelRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    void test_read_successful() throws IOException {
        File tempFile = rc.makeTempFile("read_successful", String.format("""
                    [
                        {
                            "text":"label1",
                            "uuid":"%s"
                        },
                        {
                            "text":"label2",
                            "uuid":"%s"
                        }
                    ]
                """, uuidGenerator.uuids[0], uuidGenerator.uuids[1]));
        repository = new JsonLabelRepository(tempFile);
        assertEquals(repository.getAll(), List.of(
                new Label(uuidGenerator.uuids[0], "label1"),
                new Label(uuidGenerator.uuids[1], "label2")
        ));
    }

    @Test
    void test_write_successful() throws IOException {
        File tempFile = rc.getTempFile("write_successful.json");
        repository = new JsonLabelRepository(tempFile);
        repository.create(new Label(uuidGenerator.uuids[0], "label1"));
        repository.create(new Label(uuidGenerator.uuids[1], "label2"));
        String content = Files.readString(tempFile.toPath());
        assertEquals(content.replaceAll("\\s", ""), String.format("""
                    [
                        {
                            "uuid":"%s",
                            "text":"label1"
                        },
                        {
                            "uuid":"%s",
                            "text":"label2"
                        }
                    ]
                """.replaceAll("\\s", ""), uuidGenerator.uuids[0], uuidGenerator.uuids[1]).replaceAll("\\s+", ""));
    }

    @Test
    void test_badFormat_throwsException() throws IOException {
        File tempFile = rc.getTempFile("bad_format");

        Files.writeString(tempFile.toPath(), "[1, 2, 3]");
        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());

        Files.writeString(tempFile.toPath(), "\"some string\"");
        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());

        Files.writeString(tempFile.toPath(), "{\"text\":123}");
        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    void test_missingField_throwsException() throws IOException {
        File tempFile = rc.makeTempFile("missing_field", String.format("""
                [
                    {
                        "uuid":"%s"
                    },
                    {
                        "name":"label2",
                        "uuid":"%s"
                    }
                ]
                """, uuidGenerator.uuids[0], uuidGenerator.uuids[1]));
        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    void test_extraFields_throwsException() throws IOException {
        File tempFile = rc.makeTempFile("extra_fields", String.format("""
                [
                    {
                        "text":"label1",
                        "type":"asd",
                        "uuid":"%s"
                    },
                    {
                        "text":"label2",
                        "uuid":"%s"
                    }
                ]
                """, uuidGenerator.uuids[0], uuidGenerator.uuids[1]));

        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    void test_wrongFieldType_throwsException() throws IOException {
        File tempFile = rc.makeTempFile("wrong_field_type", String.format("""
                [
                    {
                        "text":"label1",
                        "uuid":true
                    },
                    {
                        "text":"label2",
                        "uuid":"%s"
                    }
                ]
                """, uuidGenerator.uuids[1]));
        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());
    }


}
