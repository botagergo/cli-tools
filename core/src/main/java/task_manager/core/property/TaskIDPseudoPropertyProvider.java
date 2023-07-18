package task_manager.core.property;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.repository.TempIDMappingRepository;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TaskIDPseudoPropertyProvider implements PseudoPropertyProvider {
    @Override
    public Object getProperty(PropertyOwner propertyOwner) throws IOException {
        return tempIDMappingRepository.getOrCreateID(propertyOwner.getUUID());
    }
    private TempIDMappingRepository tempIDMappingRepository;
}
