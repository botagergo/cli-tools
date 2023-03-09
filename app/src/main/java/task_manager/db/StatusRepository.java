package task_manager.db;

import java.io.IOException;
import java.util.UUID;

public interface StatusRepository {
    public Status findStatus(String name) throws IOException;

    public Status getStatus(UUID uuid) throws IOException;

    public Status addStatus(String name) throws IOException;

    public void deleteAllStatuses() throws IOException;
}
