package task_manager.db.status;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import task_manager.db.JsonRepository;

public class JsonStatusRepository extends JsonRepository implements StatusRepository {

    public JsonStatusRepository(File basePath) {
        super(new File(basePath, jsonFileName));
    }

    @Override
    public Status findStatus(String name) throws IOException {
        List<Map<String, Object>> statuses = readJson();
        Optional<Map<String, Object>> status =
                statuses.stream().filter(t -> t.get("name").equals(name)).findAny();
        if (status.isPresent()) {
            try {
                return new Status(UUID.fromString((String) status.get().get("uuid")), name);
            } catch (IllegalArgumentException e) {
                throw new IOException();
            }
        } else {
            return null;
        }
    }

    @Override
    public Status getStatus(UUID uuid) throws IOException {
        List<Map<String, Object>> statuses = readJson();
        Optional<Map<String, Object>> status =
                statuses.stream().filter(t -> t.get("uuid").equals(uuid.toString())).findAny();
        if (status.isPresent()) {
            return new Status(uuid, (String) status.get().get("name"));
        } else {
            return null;
        }
    }

    @Override
    public Status addStatus(String name) throws IOException {
        List<Map<String, Object>> statuses = readJson();
        Status status = new Status(UUID.randomUUID(), name);
        statuses.add(Map.of("uuid", status.getUuid().toString(), "name", name));
        writeJson(statuses);
        return status;
    }

    @Override
    public void deleteAllStatuses() throws IOException {
        writeJson(List.of());
    }

    private static String jsonFileName = "statuses.json";

}
