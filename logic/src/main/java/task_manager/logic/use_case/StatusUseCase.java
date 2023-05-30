package task_manager.logic.use_case;

import task_manager.data.Status;

import java.io.IOException;
import java.util.UUID;

public interface StatusUseCase {
    Status getStatus(UUID uuid) throws IOException;
}
