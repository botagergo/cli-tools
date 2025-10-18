package cli_tools.common.backend.label.service;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.repository.ConstraintViolationException;
import cli_tools.common.core.repository.LabelRepository;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public UUID findLabel(@NonNull String labelType, @NonNull String labelText) {
        return labelRepository.find(labelType, labelText);
    }

    @Override
    public String getLabel(@NonNull String labelType, @NonNull UUID uuid) {
        return labelRepository.get(labelType, uuid);
    }

    @Override
    public @NonNull List<String> getLabels(@NonNull String labelType) {
        return labelRepository.getAllWithType(labelType);
    }

    @Override
    public @NonNull LinkedHashMap<String, LinkedHashMap<UUID, String>> getAllLabels() {
        return labelRepository.getAll();
    }

    @Override
    public @NonNull UUID createLabel(@NonNull String labelType, @NonNull String labelText) throws ServiceException {
        try {
            return labelRepository.create(labelType, labelText);
        } catch (ConstraintViolationException e) {
            throw new ServiceException("Label already exists: %s (%s)".formatted(labelText, labelType));
        }
    }

    @Override
    public boolean deleteLabel(@NonNull String labelType, @NonNull String labelText) {
        UUID labelUuid = labelRepository.find(labelType, labelText);
        if (labelUuid == null) {
            return false;
        }
        return labelRepository.delete(labelType, labelUuid);
    }

    @Override
    public void deleteAllLabels(@NonNull String labelType) {
        labelRepository.deleteAll(labelType);
    }

}
