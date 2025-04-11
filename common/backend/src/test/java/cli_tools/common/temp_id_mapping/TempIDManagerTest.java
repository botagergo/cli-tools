package cli_tools.common.temp_id_mapping;

import cli_tools.common.util.RoundRobinUUIDGenerator;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TempIDManagerTest {

    private final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(6);
    private TempIDManager tempIdManager;

    @Test
    public void test_getOrCreateID_getMultiple() {
        tempIdManager = new TempIDManager();

        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUUID()), 1);
        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUUID()), 2);
        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.getUUID()), 3);
    }

    @Test
    public void test_getOrCreateID_afterDeleteLatest() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.uuids[0]);
        tempIdManager.getOrCreateID(uuidGenerator.uuids[1]);
        tempIdManager.getOrCreateID(uuidGenerator.uuids[2]);

        assertTrue(tempIdManager.delete(uuidGenerator.uuids[2]));

        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.uuids[3]), 3);
    }

    @Test
    public void test_getOrCreateID_afterDeleteMiddle() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.uuids[0]);
        tempIdManager.getOrCreateID(uuidGenerator.uuids[1]);
        tempIdManager.getOrCreateID(uuidGenerator.uuids[2]);

        assertTrue(tempIdManager.delete(uuidGenerator.uuids[1]));

        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.uuids[3]), 2);
        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.uuids[4]), 4);
        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.uuids[5]), 5);
    }

    @Test
    public void test_getOrCreateID_existing() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.uuids[0]);

        assertEquals(tempIdManager.getOrCreateID(uuidGenerator.uuids[0]), 1);
    }

    @Test
    public void test_delete_empty() {
        tempIdManager = new TempIDManager();

        assertFalse(tempIdManager.delete(uuidGenerator.uuids[0]));
    }

    @Test
    public void test_delete_nonexistent() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.uuids[0]);

        assertFalse(tempIdManager.delete(uuidGenerator.uuids[1]));
    }

    @Test
    public void test_getUUID_empty() {
        tempIdManager = new TempIDManager();

        assertNull(tempIdManager.getUUID(1));
    }

    @Test
    public void test_getUUID_nonexistent() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.uuids[0]);
        assertNull(tempIdManager.getUUID(2));
    }

    @Test
    public void test_getUUID_existing() {
        tempIdManager = new TempIDManager();

        tempIdManager.getOrCreateID(uuidGenerator.uuids[0]);
        assertEquals(tempIdManager.getUUID(1), uuidGenerator.uuids[0]);
    }
}
