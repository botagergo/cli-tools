package task_manager.db;

import java.util.UUID;

public class Status {
    public Status(UUID uuid, String name) {
        if (uuid == null || name == null) {
            throw new NullPointerException();
        }

        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    private UUID uuid;
    private String name;
}
