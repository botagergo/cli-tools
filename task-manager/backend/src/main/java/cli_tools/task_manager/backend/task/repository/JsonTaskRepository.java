package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.backend.repository.JsonRepository;
import cli_tools.common.backend.repository.MapDeserializer;
import cli_tools.common.backend.repository.MapSerializer;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.util.UUIDGenerator;
import cli_tools.task_manager.backend.task.Task;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class JsonTaskRepository extends JsonRepository<List<HashMap<String, Object>>, List<Task>> implements TaskRepository {

    private final UUIDGenerator uuidGenerator;

    @Inject
    public JsonTaskRepository(@Named("taskJsonFile") File jsonPath, UUIDGenerator uuidGenerator) {
        super(jsonPath);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new MapSerializer());
        simpleModule.addDeserializer(HashMap.class, new MapDeserializer());
        getObjectMapper().registerModule(simpleModule);
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public @NonNull Task create(@NonNull Task task) throws DataAccessException {
        List<Task> tasks = getData();

        if (task.getUUID() == null) {
            task.getProperties().put(Task.UUID, uuidGenerator.getUUID());
        }

        tasks.add(task);
        writeData();

        return task;
    }

    @Override
    public Task get(@NonNull UUID uuid) throws DataAccessException {
        List<Task> tasks = getData();

        for (Task task : tasks) {
            if (Objects.equals(uuid, task.getUUID())) {
                return task;
            }
        }

        return null;
    }

    @Override
    public @NonNull List<Task> getAll() throws DataAccessException {
        return getData();
    }

    @Override
    public Task update(@NonNull Task task) throws DataAccessException {
        if (task.getUUID() == null) {
            throw new IllegalArgumentException("Missing task UUID");
        }

        List<Task> tasks = getData();

        Optional<Task> taskToUpdateOptional = tasks.stream().filter(t -> t.getUUID() == task.getUUID()).findFirst();
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
    public Task delete(@NonNull UUID uuid) throws DataAccessException {
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
    public void deleteAll() throws DataAccessException {
        getData().clear();
        writeData();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, HashMap.class);
    }

    @Override
    protected List<HashMap<String, Object>> getEmptyData() {
        return new ArrayList<>();
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
