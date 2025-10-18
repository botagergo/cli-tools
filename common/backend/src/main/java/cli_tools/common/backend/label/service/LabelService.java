package cli_tools.common.backend.label.service;

import cli_tools.common.backend.service.ServiceException;
import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public interface LabelService {
    UUID findLabel(@NonNull String labelType, @NonNull String labelText) throws ServiceException;

    String getLabel(@NonNull String labelType, @NonNull UUID uuid) throws ServiceException;

    @NonNull List<String> getLabels(@NonNull String labelType) throws ServiceException;

    @NonNull LinkedHashMap<String, LinkedHashMap<UUID, String>> getAllLabels() throws ServiceException;

    @NonNull UUID createLabel(@NonNull String labelType, @NonNull String labelText) throws ServiceException;

    boolean deleteLabel(@NonNull String labelType, @NonNull String labelText) throws ServiceException;

    void deleteAllLabels(@NonNull String labelType) throws ServiceException;
}
