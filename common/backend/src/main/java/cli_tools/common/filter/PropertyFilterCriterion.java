package cli_tools.common.filter;

import cli_tools.common.property_lib.Property;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;

import java.io.IOException;

public abstract class PropertyFilterCriterion extends FilterCriterion {

    public PropertyFilterCriterion(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        Property property = propertyManager.getProperty(propertyOwner, propertyName);
        return check_(property);
    }

    public abstract boolean check_(Property propertyValue) throws PropertyException;

    public final String propertyName;

}
