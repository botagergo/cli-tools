package task_manager.repository.temp_id_mapping;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.UUID;

@JsonSerialize
@AllArgsConstructor
public class TempIDMappings {
    public TempIDMappings() {
        this(new HashMap<>(), new TreeSet<>(), 1);
    }
    public HashMap<UUID, Integer> mappings;
    public TreeSet<Integer> freeIDs;
    public int nextID;
}