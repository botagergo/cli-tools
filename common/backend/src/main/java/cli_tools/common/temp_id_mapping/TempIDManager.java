package cli_tools.common.temp_id_mapping;

import java.util.*;

public class TempIDManager {

    public int getOrCreateID(UUID uuid) {
        int id = tempIDMappings.mappings.getOrDefault(uuid, -1);

        if (id == -1) {
            Integer newID = tempIDMappings.freeIDs.pollFirst();
            id = Objects.requireNonNullElseGet(newID, () -> tempIDMappings.nextID++);
        }

        tempIDMappings.mappings.put(uuid, id);

        return id;
    }

    public UUID getUUID(int id) {
        Optional<Map.Entry<UUID, Integer>> mapping = tempIDMappings.mappings.entrySet().stream().filter(entry -> entry.getValue() == id).findAny();
        return mapping.map(Map.Entry::getKey).orElse(null);
    }

    public boolean delete(UUID uuid) {
        Integer id = tempIDMappings.mappings.remove(uuid);

        if (id == null) {
            return false;
        }

        if (id == tempIDMappings.nextID) {
            tempIDMappings.nextID--;
        } else {
            tempIDMappings.freeIDs.add(id);
        }

        return true;
    }

    public void deleteAll() {
        tempIDMappings.mappings.clear();
        tempIDMappings.nextID = 1;
        tempIDMappings.freeIDs.clear();
    }

    protected TempIDMappings tempIDMappings = new TempIDMappings();


}
