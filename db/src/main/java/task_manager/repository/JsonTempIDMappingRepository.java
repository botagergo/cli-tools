package task_manager.repository;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JsonTempIDMappingRepository implements TempIDMappingRepository {

    @Inject
    public JsonTempIDMappingRepository(@Named("basePath") File basePath) {
        final String jsonFileName = "temp_id_mapping.json";
        this.basePath = basePath;
        this.jsonFile = new File(basePath, jsonFileName);
    }

    @Override
    public int getOrCreateID(UUID uuid) throws IOException {
        if (mappings == null) {
            loadMappings();
        }

        int id = mappings.getOrDefault(uuid, -1);

        if (id == -1) {
            Integer newID = freeIDs.pollFirst();
            id = Objects.requireNonNullElseGet(newID, () -> nextID++);
        }

        mappings.put(uuid, id);
        writeMappings();

        return id;
    }

    @Override
    public UUID getUUID(int id) throws IOException {
        if (mappings == null) {
            loadMappings();
        }

        Optional<Map.Entry<UUID, Integer>> mapping = mappings.entrySet().stream().filter(entry -> entry.getValue() == id).findAny();
        return mapping.map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
        if (mappings == null) {
            loadMappings();
        }

        Integer id = mappings.remove(uuid);

        if (id == null) {
            return false;
        }

        if (id == nextID) {
            nextID--;
        } else {
            freeIDs.add(id);
        }

        return true;
    }

    public void loadMappings() throws IOException {
        if (!basePath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            basePath.mkdirs();
        }

        if (!jsonFile.exists()) {
            mappings = new HashMap<>();
            nextID = 1;
            freeIDs = new TreeSet<>();
        } else {
            HashMap<String, Object> data = JsonMapper.readJsonMap(jsonFile);
            HashMap<String, Integer> mappingsRaw = (HashMap<String, Integer>) data.get("mappings");

            mappings = new HashMap<>(mappingsRaw.entrySet().stream()
                    .collect(Collectors.toMap(e -> UUID.fromString(e.getKey()), Map.Entry::getValue)));
            freeIDs = new TreeSet<>((List<Integer>) data.get("freeIDs"));
            nextID = (int) data.get("nextID");
        }
    }

    private void writeMappings() throws IOException {
        Map<String, Object> rawMappings = Map.of(
                "freeIDs", freeIDs.stream().toList(),
                "nextID", nextID,
                "mappings", mappings
        );
        JsonMapper.writeJsonMap(jsonFile, rawMappings);
    }

    private HashMap<UUID, Integer> mappings = null;
    private TreeSet<Integer> freeIDs;
    private int nextID;
    private final File basePath;
    private final File jsonFile;
}
