package cli_tools.common.filter;

import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;

import java.io.IOException;

public abstract class FilterCriterion {
    public boolean check(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        return check_(propertyOwner, propertyManager);
    }

    protected abstract boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException;
}
