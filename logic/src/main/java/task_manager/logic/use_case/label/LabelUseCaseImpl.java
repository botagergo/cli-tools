package task_manager.logic.use_case.label;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.data.Label;
import task_manager.core.repository.LabelRepositoryFactory;
import task_manager.core.util.UUIDGenerator;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class LabelUseCaseImpl implements LabelUseCase {

    @Override
    public Label getLabel(String labelType, UUID labelUuid) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelType).get(labelUuid);
    }

    private final LabelRepositoryFactory labelRepositoryFactory;
    private final UUIDGenerator uuidGenerator;

    @Override
    public Label findLabel(String labelType, String labelText) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelType).find(labelText);
    }

    @Override
    public Label createLabel(String labelName, String labelText) throws IOException {
        return createLabel(labelName, new Label(uuidGenerator.getUUID(), labelText));
    }

    @Override
    public Label createLabel(String labelName, Label label) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelName).create(label);
    }

    @Override
    public void deleteAllLabels(String labelName) throws IOException {
        labelRepositoryFactory.getLabelRepository(labelName).deleteAll();
    }

}
