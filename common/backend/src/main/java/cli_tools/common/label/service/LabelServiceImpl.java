package cli_tools.common.label.service;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import cli_tools.common.core.data.Label;
import cli_tools.common.core.repository.LabelRepositoryFactory;
import cli_tools.common.util.UUIDGenerator;

import java.io.IOException;
import java.util.*;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class LabelServiceImpl implements LabelService {

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
