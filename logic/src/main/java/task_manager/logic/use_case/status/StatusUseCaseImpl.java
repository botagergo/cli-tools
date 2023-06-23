package task_manager.logic.use_case.status;

import jakarta.inject.Inject;
import task_manager.core.data.Label;
import task_manager.core.data.Status;
import task_manager.core.repository.LabelRepository;
import task_manager.core.repository.LabelRepositoryFactory;
import task_manager.core.util.UUIDGenerator;

import java.io.IOException;
import java.util.UUID;

public class StatusUseCaseImpl implements StatusUseCase {

    @Inject
    public StatusUseCaseImpl(
            LabelRepositoryFactory labelRepositoryFactory,
            UUIDGenerator uuidGenerator
    ) {
        this.labelRepository = labelRepositoryFactory.getLabelRepository("status");
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
