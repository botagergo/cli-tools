package task_manager.logic.use_case.label;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.data.Label;
import task_manager.repository.LabelRepositoryFactory;
import task_manager.util.UUIDGenerator;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class LabelUseCaseImpl implements LabelUseCase {

    private final LabelRepositoryFactory labelRepositoryFactory;
    private final UUIDGenerator uuidGenerator;

    @Override
    public Label getLabel(String labelType, UUID labelUuid) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelType).get(labelUuid);
    }

    @Override
    public Label createLabel(String labelType, String labelText) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelType).create(new Label(uuidGenerator.getUUID(), labelText));
    }

}
