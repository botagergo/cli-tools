package task_manager.logic.pseudo_property_provider;

import com.fasterxml.jackson.annotation.JacksonInject;
import task_manager.logic.use_case.temp_id_mapping.TempIDMappingUseCase;
import task_manager.property_lib.*;

import java.io.IOException;

public class TempIDPseudoPropertyProvider implements PseudoPropertyProvider {
    public TempIDPseudoPropertyProvider(@JacksonInject TempIDMappingUseCase tempIDMappingUseCase) {
        this.tempIDMappingUseCase = tempIDMappingUseCase;
    }
    @Override
    public Object getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) throws IOException {
        return tempIDMappingUseCase.getOrCreateID(propertyOwner.getUUID());
    }
    private final TempIDMappingUseCase tempIDMappingUseCase;
}
