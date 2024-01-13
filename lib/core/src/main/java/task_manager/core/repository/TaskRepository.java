package task_manager.core.repository;

import lombok.NonNull;
import task_manager.core.data.Task;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskRepository {

    Task create(@NonNull Task task) throws IOException;
    Task get(@NonNull UUID uuid) throws IOException;
    List<Task> getAll() throws IOException;
    Task update(@NonNull UUID taskUuid, @NonNull Task task) throws IOException;
    boolean delete(@NonNull UUID uuid) throws IOException;
    void deleteAll() throws IOException;

}
