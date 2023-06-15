package task_manager.repository.temp_id_mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import task_manager.repository.JsonRepository;
import task_manager.repository.TempIDMappingRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonTempIDMappingRepository extends JsonRepository<TempIDMappings> implements TempIDMappingRepository {

    @Inject
    public JsonTempIDMappingRepository(@Named("tempIdMappingJsonFile") File jsonFile) {
        super(jsonFile);
    }

    @Override
    public int getOrCreateID(UUID uuid) throws IOException {
        TempIDMappings mappings = getData();

        int id = mappings.mappings.getOrDefault(uuid, -1);

        if (id == -1) {
            Integer newID = mappings.freeIDs.pollFirst();
            id = Objects.requireNonNullElseGet(newID, () -> mappings.nextID++);
        }

        mappings.mappings.put(uuid, id);

        writeData();
        return id;
    }

    @Override
    public UUID getUUID(int id) throws IOException {
        Optional<Map.Entry<UUID, Integer>> mapping = getData().mappings.entrySet().stream().filter(entry -> entry.getValue() == id).findAny();
        return mapping.map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
        TempIDMappings mappings = getData();

        Integer id = mappings.mappings.remove(uuid);

        if (id == null) {
            return false;
        }

        if (id == mappings.nextID) {
            mappings.nextID--;
        } else {
            mappings.freeIDs.add(id);
        }

        writeData();
        return true;
    }

    @Override
    public TempIDMappings getEmptyData() {
        return new TempIDMappings();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructType(TempIDMappings.class);
    }

}

@JsonSerialize
@AllArgsConstructor
class TempIDMappings {
    public TempIDMappings() {
        this(new HashMap<>(), new TreeSet<>(), 1);
    }
    public HashMap<UUID, Integer> mappings;
    public TreeSet<Integer> freeIDs;
    public int nextID;
}
