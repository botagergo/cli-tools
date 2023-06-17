package task_manager.logic.use_case.status;

import task_manager.data.Label;
import task_manager.data.Status;

import java.io.IOException;
import java.util.UUID;

public interface StatusUseCase {
    Status getStatus(UUID uuid) throws IOException;
    Label createStatus(String name) throws IOException;
}
