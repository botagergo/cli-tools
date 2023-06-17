package task_manager.logic.use_case.status;

import java.io.IOException;
import java.util.UUID;

import jakarta.inject.Inject;
import task_manager.data.Label;
import task_manager.repository.LabelRepository;
import task_manager.data.Status;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.util.UUIDGenerator;

public class StatusUseCaseImpl implements StatusUseCase {

    @Inject
    public StatusUseCaseImpl(
            LabelRepositoryFactory labelRepositoryFactory,
            UUIDGenerator uuidGenerator
    ) {
        this.labelRepository = labelRepositoryFactory.getLabelRepository("statuses");
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public Status getStatus(UUID uuid) throws IOException {
        return Status.fromLabel(labelRepository.get(uuid));
    }

    @Override
    public Label createStatus(String name) throws IOException {
        return labelRepository.create(new Label(uuidGenerator.getUUID(), name));
    }

    private final LabelRepository labelRepository;
    private final UUIDGenerator uuidGenerator;

}
