package cli_tools.common.pseudo_property_provider;

import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import cli_tools.common.temp_id_mapping.service.TempIDMappingService;
import com.fasterxml.jackson.annotation.JacksonInject;

import java.io.IOException;

public class TempIDPseudoPropertyProvider implements PseudoPropertyProvider {
    public TempIDPseudoPropertyProvider(@JacksonInject TempIDMappingService tempIDMappingService) {
        this.tempIDMappingService = tempIDMappingService;
    }
    @Override
    public Object getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) throws IOException {
        return tempIDMappingService.getOrCreateID(propertyOwner.getUUID());
    }
    private final TempIDMappingService tempIDMappingService;
}
