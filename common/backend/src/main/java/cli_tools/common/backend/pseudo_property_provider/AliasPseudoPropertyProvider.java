package cli_tools.common.backend.pseudo_property_provider;

import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AliasPseudoPropertyProvider implements PseudoPropertyProvider {
    private final String propertyName;

    public AliasPseudoPropertyProvider(@JsonProperty("propertyName") String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Object getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) throws PropertyException {
        return propertyManager.getProperty(propertyOwner, propertyName).getValue();
    }

    @Override
    public String toString() {
        return "AliasPseudoPropertyProvider(%s)".formatted(propertyName);
    }

}
