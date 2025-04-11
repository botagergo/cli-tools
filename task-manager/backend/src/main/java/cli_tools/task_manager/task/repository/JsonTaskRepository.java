package cli_tools.task_manager.task.repository;

import cli_tools.common.repository.JsonRepository;
import cli_tools.common.repository.MapDeserializer;
import cli_tools.common.repository.MapSerializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import cli_tools.task_manager.task.Task;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class JsonTaskRepository extends JsonRepository<List<HashMap<String, Object>>, List<Task>> implements TaskRepository {

    @Inject
    public JsonTaskRepository(@Named("taskJsonFile") File jsonPath) {
        super(jsonPath);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new MapSerializer());
        simpleModule.addDeserializer(HashMap.class, new MapDeserializer());
        getObjectMapper().registerModule(simpleModule);
    }

    @Override
    public @NonNull Task create(@NonNull Task task) throws IOException, IllegalArgumentException {
        List<Task> tasks = getData();

        tasks.add(task);
        writeData();

        return task;
    }

    @Override
    public Task get(@NonNull UUID uuid) throws IOException {
        List<Task> tasks = getData();

        for (Task task : tasks) {
            if (Objects.equals(uuid, task.getUUID())) {
                return task;
            }
        }

        return null;
    }

    @Override
    public List<Task> getAll() throws IOException {
        return getData();
    }

    @Override
    protected List<HashMap<String, Object>> getEmptyData() {
        return new ArrayList<>();
    }

    @Override
    public Task update(@NonNull UUID taskUuid, @NonNull Task task) throws IOException {
        List<Task> tasks = getData();

        Optional<Task> taskToUpdateOptional = tasks.stream().filter(t -> t.getUUID() == taskUuid).findFirst();
        if (taskToUpdateOptional.isEmpty()) {
            return null;
        }

        Task taskToUpdate = taskToUpdateOptional.get();
        for (Map.Entry<String, Object> pair : task.getProperties().entrySet()) {
            taskToUpdate.getProperties().put(pair.getKey(), pair.getValue());
        }

        writeData();
        return taskToUpdate;
    }

    @Override
    public Task delete(@NonNull UUID uuid) throws IOException {
        List<Task> tasks = getData();

        for (Task task : tasks) {
            if (task.getUUID().equals(uuid)) {
                tasks.remove(task);
                writeData();
                return task;
            }
        }

        return null;
    }

    @Override
    public void deleteAll() throws IOException {
        getData().clear();
        writeData();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, HashMap.class);
    }

    @Override
    protected ArrayList<Task> jsonToStoredData(List<HashMap<String, Object>> data) {
        return data.stream().map(Task::fromMap).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    protected List<HashMap<String, Object>> storedToJsonData(List<Task> data) {
        return data.stream().map(Task::getProperties).collect(Collectors.toList());
    }

}
