package task_manager.db;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TaskRepository {

    public void addTask(Map<String, Object> task) throws IOException;

    public void modifyTask(Map<String, Object> task) throws IOException;

    public List<Map<String, Object>> getTasks() throws IOException;

}
