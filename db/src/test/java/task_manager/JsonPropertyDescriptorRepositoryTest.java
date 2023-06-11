package task_manager;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task_manager.property.PropertyDescriptor;
import task_manager.repository.JsonPropertyDescriptorRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.testng.Assert.*;

public class JsonPropertyDescriptorRepositoryTest {

    @BeforeClass
    public void setupClass() throws IOException {
        tempDir = Files.createTempDirectory("testng");
    }

    @Test
    public void test_read_successful() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "read_successful", ".json");
        Files.writeString(tempFile, "{\"name\":{\"name\":\"name\",\"type\":\"String\",\"multiplicity\":\"SINGLE\",\"defaultValue\":null},\"other_name\":{\"name\":\"other_name\",\"type\":\"UUID\",\"multiplicity\":\"SET\",\"defaultValue\":null}}");
        repository = new JsonPropertyDescriptorRepository(tempFile.toFile());
        assertEquals(repository.getAll(), List.of(
                        new PropertyDescriptor("name", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null),
                        new PropertyDescriptor("other_name", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null)
        ));
    }

    @Test
    public void test_write_successful() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "write_successful", ".json");
        Files.writeString(tempFile, "{\"name\":{\"name\":\"name\",\"type\":\"String\",\"multiplicity\":\"SINGLE\",\"defaultValue\":null},\"other_name\":{\"name\":\"other_name\",\"type\":\"UUID\",\"multiplicity\":\"SET\",\"defaultValue\":null}}");

        repository = new JsonPropertyDescriptorRepository(tempFile.toFile());
        repository.create(new PropertyDescriptor("name", PropertyDescriptor.Type.String, PropertyDescriptor.Multiplicity.SINGLE, null));
        repository.create(new PropertyDescriptor("other_name", PropertyDescriptor.Type.UUID, PropertyDescriptor.Multiplicity.SET, null));

        String content = Files.readString(tempFile);
        assertEquals(content,
                "{\"name\":{\"name\":\"name\",\"type\":\"String\",\"multiplicity\":\"SINGLE\",\"defaultValue\":null},\"other_name\":{\"name\":\"other_name\",\"type\":\"UUID\",\"multiplicity\":\"SET\",\"defaultValue\":null}}"
        );
    }

    @Test
    public void test_badFormat_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "bad_format", ".json");

        Files.writeString(tempFile, "[1, 2, 3]");
        repository = new JsonPropertyDescriptorRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.get("property"));

        Files.writeString(tempFile, "\"some string\"");
        repository = new JsonPropertyDescriptorRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.get("property"));

        Files.writeString(tempFile, "{\"name\":123}");
        repository = new JsonPropertyDescriptorRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    @Test
    public void test_missingField_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "missing_field", ".json");
        Files.writeString(tempFile, "{\"name\":{\"name\":\"name\",\"type\":\"String\",\"multiplicity\":\"SINGLE\"}}");
        repository = new JsonPropertyDescriptorRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    @Test
    public void test_extraFields_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "extra_fields", ".json");
        Files.writeString(tempFile, "{\"name\":{\"name\":\"name\",\"type\":\"String\",\"multiplicity\":\"SINGLE\",\"defaultValue\":null, \"extra_field\":null}}");
        repository = new JsonPropertyDescriptorRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    @Test
    public void test_wrongFieldType_throwsException() throws IOException {
        Path tempFile = Files.createTempFile(tempDir, "wrong_field_type", ".json");
        Files.writeString(tempFile, "{\"name\":{\"name\":\"name\",\"type\":123,\"multiplicity\":\"SINGLE\",\"defaultValue\":null},\"other_name\":{\"name\":\"other_name\",\"type\":\"UUID\",\"multiplicity\":\"SET\",\"defaultValue\":null}}");
        repository = new JsonPropertyDescriptorRepository(tempFile.toFile());
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    private JsonPropertyDescriptorRepository repository;
    private Path tempDir;

}
