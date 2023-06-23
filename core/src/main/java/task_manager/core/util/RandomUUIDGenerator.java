package task_manager.core.util;

import java.util.UUID;

public class RandomUUIDGenerator implements UUIDGenerator {
    @Override
    public UUID getUUID() {
        return UUID.randomUUID();
    }
}
