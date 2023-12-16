package task_manager.task_logic.use_case.temp_id_mapping;

import java.io.IOException;
import java.util.UUID;

public interface TempIDMappingUseCase {
    int getOrCreateID(UUID uuid) throws IOException;
    UUID getUUID(int id) throws IOException;
    boolean delete(UUID uuid) throws IOException;
}
