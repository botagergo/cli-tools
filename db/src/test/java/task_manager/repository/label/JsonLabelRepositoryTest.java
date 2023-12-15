package task_manager.repository.label;

import org.testng.annotations.Test;
import task_manager.core.data.Label;
import task_manager.repository.util.JsonRepositoryCreator;
import task_manager.util.RoundRobinUUIDGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class JsonLabelRepositoryTest {

    public JsonLabelRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    public void test_read_successful() throws IOException {
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
        """, uuidGenerator.getUUID(0), uuidGenerator.getUUID(1)));
        repository = new JsonLabelRepository(tempFile);
        assertEquals(repository.getAll(), List.of(
                new Label(uuidGenerator.getUUID(0), "label1"),
                new Label(uuidGenerator.getUUID(1), "label2")
        ));
    }

    @Test
    public void test_write_successful() throws IOException {
        File tempFile = rc.getTempFile("write_successful.json");
        repository = new JsonLabelRepository(tempFile);
        repository.create(new Label(uuidGenerator.getUUID(0), "label1"));
        repository.create(new Label(uuidGenerator.getUUID(1), "label2"));
        String content = Files.readString(tempFile.toPath());
        assertEquals(content, String.format("""
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
        """, uuidGenerator.getUUID(0), uuidGenerator.getUUID(1)).replaceAll("\\s+",""));
    }

    @Test
    public void test_badFormat_throwsException() throws IOException {
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
    public void test_missingField_throwsException() throws IOException {
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
        """, uuidGenerator.getUUID(0), uuidGenerator.getUUID(1)));
        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_extraFields_throwsException() throws IOException {
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
            """, uuidGenerator.getUUID(0), uuidGenerator.getUUID(1)));
        
        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_wrongFieldType_throwsException() throws IOException {
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
        """, uuidGenerator.getUUID(1)));
        repository = new JsonLabelRepository(tempFile);
        assertThrows(IOException.class, () -> repository.getData());
    }

    private JsonLabelRepository repository;
    final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final JsonRepositoryCreator rc;


}
