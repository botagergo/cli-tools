package task_manager.repository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import task_manager.data.Task;

public interface TaskRepository {

    Task create(Task task) throws IOException;
    Task get(UUID uuid) throws IOException;
    List<Task> getAll() throws IOException;
    Task update(Task task) throws IOException;
    boolean delete(UUID uuid) throws IOException;
    void deleteAll() throws IOException;

}
