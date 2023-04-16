package task_manager.db.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import task_manager.db.JsonRepository;
import task_manager.db.property.Property;
import task_manager.db.property.PropertyException;

public class JsonTaskRepository extends JsonRepository implements TaskRepository {

    public JsonTaskRepository(File basePath) {
        super(new File(basePath, jsonFileName));
    }

    @Override
    public Task addTask(Task task) throws IOException, IllegalArgumentException {
        try {
            List<Task> tasks = getTasks();
            task.setUuid(UUID.randomUUID());
            tasks.add(task);
            writeJson(tasks.stream().map(task_ -> task_.asMap()).collect(Collectors.toList()));
            return task;
        } catch (PropertyException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Task modifyTask(Task task) throws IOException, IllegalArgumentException {
        try {
            List<Task> tasks = getTasks();

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

            for (Pair<String, Property> pair : task.getPropertiesIter()) {
                if (pair.getKey() != "uuid") {
                    taskToUpdate.setProperty(pair.getKey(), pair.getValue().getValue());
                }
            }

            writeTasks(tasks);
            return taskToUpdate;
        } catch (PropertyException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public List<Task> getTasks() throws IOException {
        List<Task> tasks = new ArrayList<>();
        Iterator<HashMap<String, Object>> taskIter = readJson().stream().iterator();
        while (taskIter.hasNext()) {
            tasks.add(Task.fromMap(taskIter.next()));
        } ;

        return tasks;
    }

    private void writeTasks(List<Task> tasks) throws IOException {
        List<HashMap<String, Object>> converted_tasks =
                tasks.stream().map(task -> task.asMap()).collect(Collectors.toList());
        writeJson(converted_tasks);
    }

    @Override
    public void deleteAllTasks() throws IOException {
        writeJson(List.of());
    }

    private static String jsonFileName = "tasks.json";
}
