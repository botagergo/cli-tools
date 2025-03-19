package cli_tools.common.core.repository;

import java.io.IOException;
import java.util.UUID;

public interface TempIDMappingRepository {

    int getOrCreateID(UUID uuid) throws IOException;
    UUID getUUID(int id) throws IOException;
    boolean delete(UUID uuid) throws  IOException;
    void deleteAll() throws IOException;
}
