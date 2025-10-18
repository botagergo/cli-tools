package cli_tools.common.core.repository;

import lombok.NonNull;

import java.util.List;

public interface OrderedLabelRepository {

    boolean create(@NonNull String type, @NonNull String text, int value) throws DataAccessException;

    String get(@NonNull String type, int value) throws DataAccessException;

    @NonNull List<String> getAll(@NonNull String type) throws DataAccessException;

    Integer find(@NonNull String type, @NonNull String text) throws DataAccessException;

    void deleteAll(@NonNull String type) throws DataAccessException;

}
