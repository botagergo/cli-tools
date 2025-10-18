package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.core.repository.DataAccessException;
import cli_tools.task_manager.backend.task.Task;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface TaskRepository {

    @NonNull Task create(@NonNull Task task) throws DataAccessException;

    Task get(@NonNull UUID uuid) throws DataAccessException;

    @NonNull List<Task> getAll() throws DataAccessException;

    Task update(@NonNull Task task) throws DataAccessException;

    Task delete(@NonNull UUID uuid) throws DataAccessException;

    void deleteAll() throws DataAccessException;

}
