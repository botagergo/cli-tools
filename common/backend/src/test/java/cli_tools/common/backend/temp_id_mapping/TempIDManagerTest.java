package cli_tools.common.backend.temp_id_mapping;

import cli_tools.common.util.RoundRobinUUIDGenerator;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TempIDManagerTest {

    private final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(6);
    private TempIDManager tempIdManager;

    @Test
    void test_getOrCreateID_getMultiple() {
        tempIdManager = new TempIDManager();

        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUUID()), 1);
        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUUID()), 2);
        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUUID()), 3);
    }

    @Test
    void test_getOrCreateID_afterDeleteLatest() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[0]);
        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[1]);
        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[2]);

        assertTrue(tempIdManager.delete(uuidGenerator.getUuids()[2]));

        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUuids()[3]), 3);
    }

    @Test
    void test_getOrCreateID_afterDeleteMiddle() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[0]);
        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[1]);
        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[2]);

        assertTrue(tempIdManager.delete(uuidGenerator.getUuids()[1]));

        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUuids()[3]), 2);
        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUuids()[4]), 4);
        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUuids()[5]), 5);
    }

    @Test
    void test_getOrCreateID_existing() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[0]);

        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUuids()[0]), 1);
    }

    @Test
    void test_delete_empty() {
        tempIdManager = new TempIDManager();

        assertFalse(tempIdManager.delete(uuidGenerator.getUuids()[0]));
    }

    @Test
    void test_delete_nonexistent() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[0]);

        assertFalse(tempIdManager.delete(uuidGenerator.getUuids()[1]));
    }

    @Test
    void test_getUUID_empty() {
        tempIdManager = new TempIDManager();

        assertNull(tempIdManager.getUUID(1));
    }

    @Test
    void test_getUUID_nonexistent() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[0]);
        assertNull(tempIdManager.getUUID(2));
    }

    @Test
    void test_getUUID_existing() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.getUuids()[0]);
        assertEquals(tempIdManager.getUUID(1), uuidGenerator.getUuids()[0]);
    }
}
