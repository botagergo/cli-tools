package task_manager.task_logic.pseudo_property_provider;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.task_logic.use_case.temp_id_mapping.TempIDMappingUseCase;
import task_manager.property_lib.PropertyOwner;
import task_manager.property_lib.PseudoPropertyProvider;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TaskIDPseudoPropertyProvider implements PseudoPropertyProvider {
    @Override
    public Object getProperty(PropertyOwner propertyOwner) throws IOException {
        return tempIDMappingUseCase.getOrCreateID(propertyOwner.getUUID());
    }
    private TempIDMappingUseCase tempIDMappingUseCase;
}
