package task_manager.core.data;

import org.testng.annotations.Test;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.core.util.UUIDGenerator;
import task_manager.core.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class TaskTest {

    @Test
    public void test_getUUID_successful() {
        assertEquals(Task.fromMap(Utils.newHashMap("uuid", uuid)).getUUID(), uuid);
    }

    @Test
    public void test_getUUID_notDefined_returnsNull() {
        assertNull(Task.fromMap(new HashMap<>()).getUUID());
    }

    @Test
    public void test_getUUID_wrongType_returnsNull() {
        assertNull(Task.fromMap(new HashMap<>(Map.of("uuid", 123))).getUUID());
    }

    @Test
    public void test_getUUID_badUUID_returnsNull() {
        assertNull(Task.fromMap(new HashMap<>(Map.of("uuid", "asdf"))).getUUID());
    }

    private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(1);

    private final UUID uuid = uuidGenerator.getUUID();
}
