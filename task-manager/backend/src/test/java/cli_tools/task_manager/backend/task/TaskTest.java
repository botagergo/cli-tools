package cli_tools.task_manager.backend.task;

import cli_tools.common.util.UUIDGenerator;
import cli_tools.common.util.Utils;
import cli_tools.test_utils.RoundRobinUUIDGenerator;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class TaskTest {

    private final UUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator(1);
    private final UUID uuid = uuidGenerator.getUUID();

    @Test
    void test_getUUID_successful() {
        assertEquals(Task.fromMap(Utils.newHashMap("uuid", uuid)).getUUID(), uuid);
    }

    @Test
    void test_getUUID_notDefined_returnsNull() {
        assertNull(Task.fromMap(new HashMap<>()).getUUID());
    }

    @Test
    void test_getUUID_wrongType_returnsNull() {
        assertNull(Task.fromMap(new HashMap<>(Map.of("uuid", 123))).getUUID());
    }

    @Test
    void test_getUUID_badUUID_returnsNull() {
        assertNull(Task.fromMap(new HashMap<>(Map.of("uuid", "asdf"))).getUUID());
    }
}
