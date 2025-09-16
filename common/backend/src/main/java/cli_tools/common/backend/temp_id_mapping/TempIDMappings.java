package cli_tools.common.backend.temp_id_mapping;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.UUID;

@JsonSerialize
@AllArgsConstructor
public class TempIDMappings {
    public HashMap<UUID, Integer> mappings;
    public TreeSet<Integer> freeIDs;
    public int nextID;
    public TempIDMappings() {
        this(new HashMap<>(), new TreeSet<>(), 1);
    }
}