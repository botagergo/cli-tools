package task_manager.logic.use_case.label;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.data.Label;
import task_manager.core.repository.LabelRepositoryFactory;
import task_manager.util.UUIDGenerator;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class LabelUseCaseImpl implements LabelUseCase {

    @Override
    public Label getLabel(String labelType, UUID labelUuid) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelType).get(labelUuid);
    }

    @Override
    public Label findLabel(String labelType, String labelText) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelType).find(labelText);
    }

    @Override
    public Label createLabel(String labelType, String labelText) throws IOException {
        return createLabel(labelType, new Label(uuidGenerator.getUUID(), labelText));
    }

    @Override
    public Label createLabel(String labelType, Label label) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelType).create(label);
    }

    @Override
    public void deleteAllLabels(String labelType) throws IOException {
        labelRepositoryFactory.getLabelRepository(labelType).deleteAll();
    }

    @Override
    public List<Label> getLabels(String labelType) throws IOException {
        return labelRepositoryFactory.getLabelRepository(labelType).getAll();
    }

    private final LabelRepositoryFactory labelRepositoryFactory;
    private final UUIDGenerator uuidGenerator;

}
