package task_manager;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task_manager.core.data.Label;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.repository.label.JsonLabelRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class JsonLabelRepositoryTest {

    @BeforeClass
    public void setupClass() throws IOException {
        tempDir = Files.createTempDirectory("testng");
    }

    @Test
    public void test_read_successful() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "read_successful", ".json");
        Files.writeString(tempFile, "[{\"text\":\"label1\",\"uuid\":\"" + uuidGenerator.getUUID(0) + "\"},{\"text\":\"label2\",\"uuid\":\"" + uuidGenerator.getUUID(1) + "\"}]");
        repository = new JsonLabelRepository(tempFile.toFile());
        assertEquals(repository.getAll(), List.of(
                new Label(uuidGenerator.getUUID(0), "label1"),
                new Label(uuidGenerator.getUUID(1), "label2")
        ));
    }

    @Test
    public void test_write_successful() throws IOException {
        File tempFile = Paths.get(tempDir.toString(), "write_successful.json").toFile();
        repository = new JsonLabelRepository(tempFile);
        repository.create(new Label(uuidGenerator.getUUID(0), "label1"));
        repository.create(new Label(uuidGenerator.getUUID(1), "label2"));
        String content = Files.readString(tempFile.toPath());
        assertEquals(content,
                "[{\"uuid\":\"" + uuidGenerator.getUUID(0)
                        + "\",\"text\":\"label1\"},{\"uuid\":\"" + uuidGenerator.getUUID(1)
                        + "\",\"text\":\"label2\"}]"
        );
    }

    @Test
    public void test_badFormat_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "bad_format", ".json");

        Files.writeString(tempFile, "[1, 2, 3]");
        repository = new JsonLabelRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());

        Files.writeString(tempFile, "\"some string\"");
        repository = new JsonLabelRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());

        Files.writeString(tempFile, "{\"text\":123}");
        repository = new JsonLabelRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_missingField_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "missing_field", ".json");
        Files.writeString(tempFile, "[{\"uuid\":\"" + uuidGenerator.getUUID(0) + "\"},{\"name\":\"label2\",\"uuid\":\"" + uuidGenerator.getUUID(1) + "\"}]");
        repository = new JsonLabelRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_extraFields_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "extra_fields", ".json");
        Files.writeString(tempFile, "[{\"text\":\"label1\",\"type\":\"asd\",\"uuid\":\"" + uuidGenerator.getUUID(0) + "\"},{\"text\":\"label2\",\"uuid\":\"" + uuidGenerator.getUUID(1) + "\"}]");
        repository = new JsonLabelRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());
    }

    @Test
    public void test_wrongFieldType_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "wrong_field_type", ".json");
        Files.writeString(tempFile, "[{\"text\":\"label1\",\"uuid\":true},{\"text\":\"label2\",\"uuid\":\"" + uuidGenerator.getUUID(1) + "\"}]");
        repository = new JsonLabelRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.getData());
    }

    private JsonLabelRepository repository;
    RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private Path tempDir;

}
