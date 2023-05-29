package task_manager.logic.use_case;

import java.io.IOException;
import java.util.UUID;

import jakarta.inject.Inject;
import task_manager.repository.LabelRepository;
import task_manager.data.Status;
import task_manager.repository.LabelRepositoryFactory;

public class StatusUseCase {

    @Inject
    public StatusUseCase(LabelRepositoryFactory labelRepositoryFactory) {
        this.labelRepository = labelRepositoryFactory.getLabelRepository("status");
    }

    public Status getStatus(UUID uuid) throws IOException {
        return Status.fromLabel(labelRepository.get(uuid));
    }

    private final LabelRepository labelRepository;

}
