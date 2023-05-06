package task_manager.logic.use_case;

import java.io.IOException;
import java.util.UUID;

import jakarta.inject.Inject;
import task_manager.annotation.StatusAnnotation;
import task_manager.data.Label;
import task_manager.repository.LabelRepository;
import task_manager.data.Status;

public class StatusUseCase {

    @Inject
    public StatusUseCase(@StatusAnnotation LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public Status createStatus(String statusName) throws IOException {
        return Status.fromLabel(labelRepository.create(labelRepository.create(new Label(UUID.randomUUID(), statusName))));
    }

    public Status findStatus(String statusName) throws IOException {
        return Status.fromLabel(labelRepository.find(statusName));
    }

    public Status getStatus(UUID uuid) throws IOException {
        return Status.fromLabel(labelRepository.get(uuid));
    }

    public void deleteAllStatuses() throws IOException {
        labelRepository.deleteAll();
    }

    private final LabelRepository labelRepository;

}
