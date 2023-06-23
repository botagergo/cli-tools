package task_manager.core.repository;

import task_manager.core.data.Task;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface TaskRepository {

    Task create(Task task) throws IOException;
    Task get(UUID uuid) throws IOException;
    List<Task> getAll() throws IOException;
    Task update(Task task) throws IOException;
    boolean delete(UUID uuid) throws IOException;
    void deleteAll() throws IOException;

}
