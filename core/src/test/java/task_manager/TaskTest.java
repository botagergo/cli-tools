package task_manager;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import task_manager.data.Task;
import task_manager.util.RoundRobinUUIDGenerator;
import task_manager.util.UUIDGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskTest {

    @Test
    public void test_getUUID_successful() {
        assertEquals(Task.fromMap(new HashMap<>(Map.of("uuid", uuid.toString()))).getUUID(), uuid);
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

    private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();

    private final UUID uuid = uuidGenerator.getUUID();
}
