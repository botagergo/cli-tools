package cli_tools.common.temp_id_mapping.service;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import cli_tools.common.core.repository.TempIDMappingRepository;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TempIDMappingServiceImpl implements TempIDMappingService {

    @Override
    public int getOrCreateID(UUID uuid) throws IOException {
        return tempIDMappingRepository.getOrCreateID(uuid);
    }

    @Override
    public UUID getUUID(int id) throws IOException {
        return tempIDMappingRepository.getUUID(id);
    }

    @Override
    public void delete(UUID uuid) throws IOException {
        tempIDMappingRepository.delete(uuid);
    }

    @Override
    public void deleteAll() throws IOException {
        tempIDMappingRepository.deleteAll();
    }

    private final TempIDMappingRepository tempIDMappingRepository;

}
