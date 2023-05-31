package task_manager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import task_manager.repository.JsonTempIDMappingRepository;
import task_manager.util.RoundRobinUUIDGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.testng.Assert.*;

public class JsonTempIdMappingRepositoryTest {

    @BeforeMethod
    public void setup() throws IOException {
        Path tempDir = Files.createTempDirectory("testng");
        repository = new JsonTempIDMappingRepository(tempDir.toFile());
    }

    @Test
    public void test_getOrCreateID_getMultiple() throws IOException {
        assertEquals(repository.getOrCreateID(uuidGenerator.getUUID()), 1);
        assertEquals(repository.getOrCreateID(uuidGenerator.getUUID()), 2);
        assertEquals(repository.getOrCreateID(uuidGenerator.getUUID()), 3);
    }

    @Test
    public void test_getOrCreateID_afterDeleteLatest() throws IOException {
        repository.getOrCreateID(uuidGenerator.getUUID(0));
        repository.getOrCreateID(uuidGenerator.getUUID(1));
        repository.getOrCreateID(uuidGenerator.getUUID(2));

        assertTrue(repository.delete(uuidGenerator.getUUID(2)));

        assertEquals(repository.getOrCreateID(uuidGenerator.getUUID(3)), 3);
    }

    @Test
    public void test_getOrCreateID_afterDeleteMiddle() throws IOException {
        repository.getOrCreateID(uuidGenerator.getUUID(0));
        repository.getOrCreateID(uuidGenerator.getUUID(1));
        repository.getOrCreateID(uuidGenerator.getUUID(2));

        assertTrue(repository.delete(uuidGenerator.getUUID(1)));

        assertEquals(repository.getOrCreateID(uuidGenerator.getUUID(3)), 2);
        assertEquals(repository.getOrCreateID(uuidGenerator.getUUID(4)), 4);
        assertEquals(repository.getOrCreateID(uuidGenerator.getUUID(5)), 5);
    }

    @Test
    public void test_getOrCreateID_existing() throws IOException {
        repository.getOrCreateID(uuidGenerator.getUUID(0));

        assertEquals(repository.getOrCreateID(uuidGenerator.getUUID(0)), 1);
    }

    @Test
    public void test_delete_empty() throws IOException {
        assertFalse(repository.delete(uuidGenerator.getUUID(0)));
    }

    @Test
    public void test_delete_nonexistent() throws IOException {
        repository.getOrCreateID(uuidGenerator.getUUID(0));

        assertFalse(repository.delete(uuidGenerator.getUUID(1)));
    }

    @Test
    public void test_getUUID_empty() throws IOException {
        assertNull(repository.getUUID(1));
    }

    @Test
    public void test_getUUID_nonexistent() throws IOException {
        repository.getOrCreateID(uuidGenerator.getUUID(0));
        assertNull(repository.getUUID(2));
    }

    @Test
    public void test_getUUID_existing() throws IOException {
        repository.getOrCreateID(uuidGenerator.getUUID(0));
        assertEquals(repository.getUUID(1), uuidGenerator.getUUID(0));
    }

    private JsonTempIDMappingRepository repository;
    private final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(6);
}
