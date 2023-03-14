package task_manager.db.task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.db.JsonRepository;
import task_manager.db.property.PropertyException;

public class JsonTaskRepository extends JsonRepository implements TaskRepository {

    public JsonTaskRepository(File basePath) {
        super(new File(basePath, jsonFileName));
    }

    @Override
    public Task addTask(Task task) throws IOException, IllegalArgumentException {
        try {
            List<Map<String, Object>> tasks = readJson();
            task.setUuid(UUID.randomUUID());
            tasks.add(task.asMap());
            writeJson(tasks);
            return task;
        } catch (PropertyException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Task modifyTask(Task task) throws IOException, IllegalArgumentException {
        try {
            List<Task> tasks = readJson().stream().map(taskMap -> Task.fromMap(taskMap))
                    .collect(Collectors.toList());

            Task taskToUpdate = null;

            for (Task taskToUpdate_ : tasks) {
                if (taskToUpdate_.getUuid().equals(task.getUuid())) {
                    taskToUpdate = taskToUpdate_;
                    break;
                }
            }

            if (taskToUpdate == null) {
                throw new IllegalArgumentException("No such task: " + task.getName());
            }

            for (Pair<String, Object> pair : task.getPropertiesIter()) {
                if (pair.getKey() != "uuid") {
                    taskToUpdate.setProperty(pair.getKey(), pair.getValue());
                }
            }

            taskToUpdate.setDone(task.getDone());
            writeJson(tasks.stream().map(task_ -> task_.asMap()).collect(Collectors.toList()));
            return taskToUpdate;
        } catch (PropertyException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<Task> getTasks() throws IOException {
        return readJson().stream().map(taskMap -> Task.fromMap(taskMap))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllTasks() throws IOException {
        writeJson(List.of());
    }

    private static String jsonFileName = "tasks.json";

}
