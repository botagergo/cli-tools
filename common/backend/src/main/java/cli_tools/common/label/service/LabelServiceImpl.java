package cli_tools.common.label.service;

import cli_tools.common.core.repository.LabelRepository;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public boolean labelExists(String labelType, String labelText) throws IOException {
        return labelRepository.exists(labelType, labelText);
    }

    @Override
    public List<String> getLabels(String labelType) throws IOException {
        return labelRepository.getAllWithType(labelType);
    }

    @Override
    public Map<String, List<String>> getAllLabels() throws IOException {
        return labelRepository.getAll();
    }

    @Override
    public boolean createLabel(String labelType, String labelText) throws IOException {
        return labelRepository.create(labelType, labelText);
    }

    @Override
    public boolean deleteLabel(String labelType, String labelText) throws IOException {
        return labelRepository.delete(labelType, labelText);
    }

    @Override
    public void deleteAllLabels(String labelType) throws IOException {
        labelRepository.deleteAll(labelType);
    }

}
