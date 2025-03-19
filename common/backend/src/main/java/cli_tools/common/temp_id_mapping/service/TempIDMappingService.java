package cli_tools.common.temp_id_mapping.service;

import java.io.IOException;
import java.util.UUID;

public interface TempIDMappingService {
    int getOrCreateID(UUID uuid) throws IOException;
    UUID getUUID(int id) throws IOException;
    void delete(UUID uuid) throws IOException;
    void deleteAll() throws IOException;
}
