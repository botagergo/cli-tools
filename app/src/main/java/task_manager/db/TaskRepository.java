package task_manager.db;

import java.io.IOException;
import java.util.List;

public interface TaskRepository {

    public Task addTask(Task task) throws IOException;

    public Task modifyTask(Task task) throws IOException;

    public List<Task> getTasks() throws IOException;

}
