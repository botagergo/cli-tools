package task_manager.api.use_case;

import java.io.IOException;
import java.util.UUID;
import com.google.inject.Inject;
import task_manager.db.status.Status;
import task_manager.db.status.StatusRepository;

public class StatusUseCase {

    public Status addStatus(String statusName) throws IOException {
        return statusRepository.addStatus(statusName);
    }

    public Status findStatus(String statusName) throws IOException {
        return statusRepository.findStatus(statusName);
    }

    public task_manager.db.status.Status getStatus(UUID uuid) throws IOException {
        return statusRepository.getStatus(uuid);
    }

    public void deleteAllStatuses() throws IOException {
        statusRepository.deleteAllStatuses();
    }

    @Inject
    private StatusRepository statusRepository;

}
