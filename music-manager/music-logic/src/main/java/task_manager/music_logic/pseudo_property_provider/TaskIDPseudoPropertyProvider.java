package common.music_logic.pseudo_property_provider;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import common.music_logic.use_case.temp_id_mapping.TempIDMappingUseCase;
import common.property_lib.PropertyOwner;
import common.property_lib.PseudoPropertyProvider;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class TaskIDPseudoPropertyProvider implements PseudoPropertyProvider {
    @Override
    public Object getProperty(PropertyOwner propertyOwner) throws IOException {
        return tempIDMappingUseCase.getOrCreateID(propertyOwner.getUUID());
    }
    private TempIDMappingUseCase tempIDMappingUseCase;
}
