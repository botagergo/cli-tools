package cli_tools.common.pseudo_property_provider;

import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import cli_tools.common.temp_id_mapping.TempIDManager;
import com.fasterxml.jackson.annotation.JacksonInject;

import java.io.IOException;

public class TempIDPseudoPropertyProvider implements PseudoPropertyProvider {
    private final TempIDManager tempIdManager;

    public TempIDPseudoPropertyProvider(@JacksonInject TempIDManager tempIdManager) {
        this.tempIdManager = tempIdManager;
    }

    @Override
    public Object getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) throws IOException {
        return tempIdManager.getOrCreateID(propertyOwner.getUUID());
    }
}
