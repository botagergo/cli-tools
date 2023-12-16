package task_manager.music_logic.use_case.temp_id_mapping;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.repository.TempIDMappingRepository;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TempIDMappingUseCaseImpl implements TempIDMappingUseCase {

    @Override
    public int getOrCreateID(UUID uuid) throws IOException {
        return tempIDMappingRepository.getOrCreateID(uuid);
    }

    @Override
    public UUID getUUID(int id) throws IOException {
        return tempIDMappingRepository.getUUID(id);
    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
        return tempIDMappingRepository.delete(uuid);
    }

    private final TempIDMappingRepository tempIDMappingRepository;

}
