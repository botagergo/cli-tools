package task_manager.logic.pseudo_property_provider;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.property.PropertyOwner;
import task_manager.core.property.PseudoPropertyProvider;
import task_manager.logic.use_case.temp_id_mapping.TempIDMappingUseCase;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TaskIDPseudoPropertyProvider implements PseudoPropertyProvider {
    @Override
    public Object getProperty(PropertyOwner propertyOwner) throws IOException {
        return tempIDMappingUseCase.getOrCreateID(propertyOwner.getUUID());
    }
    private TempIDMappingUseCase tempIDMappingUseCase;
}
