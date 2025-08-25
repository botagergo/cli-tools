package cli_tools.common.label.service;

import cli_tools.common.core.repository.LabelRepository;
import cli_tools.common.util.UUIDGenerator;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public boolean labelExists(String labelType, String labelText) throws IOException {
        return labelRepository.exists(labelType, labelText);
    }

    @Override
    public List<String> getLabels(String labelType) throws IOException {
        return labelRepository.getAll(labelType);
    }

    @Override
    public boolean createLabel(String labelType, String labelText) throws IOException {
        return labelRepository.create(labelType, labelText);
    }

    @Override
    public void deleteAllLabels(String labelType) throws IOException {
        labelRepository.deleteAll(labelType);
    }

}
