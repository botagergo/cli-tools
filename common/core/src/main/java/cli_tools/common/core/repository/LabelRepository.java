package cli_tools.common.core.repository;

import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public interface LabelRepository {

    @NonNull UUID create(@NonNull String labelType, @NonNull String labelText) throws DataAccessException;

    String get(@NonNull String labelType, @NonNull UUID uuid) throws DataAccessException;

    UUID find(@NonNull String labelType, @NonNull String labelText) throws DataAccessException;

    @NonNull List<String> getAllWithType(@NonNull String labelType) throws DataAccessException;

    @NonNull LinkedHashMap<String, LinkedHashMap<UUID, String>> getAll() throws DataAccessException;

    boolean delete(@NonNull String labelType, @NonNull UUID uuid) throws DataAccessException;

    void deleteAll(@NonNull String labelType) throws DataAccessException;

}
