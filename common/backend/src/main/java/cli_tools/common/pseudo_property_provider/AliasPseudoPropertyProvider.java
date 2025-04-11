package cli_tools.common.pseudo_property_provider;

import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

public class AliasPseudoPropertyProvider implements PseudoPropertyProvider {
    private final String propertyName;

    public AliasPseudoPropertyProvider(@JsonProperty("propertyName") String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Object getProperty(PropertyManager propertyManager, PropertyOwner propertyOwner) throws IOException, PropertyException {
        return propertyManager.getProperty(propertyOwner, propertyName).getValue();
    }
}
