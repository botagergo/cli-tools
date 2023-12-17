package task_manager.logic.filter;

import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyManager;
import task_manager.property_lib.PropertyOwner;

import java.io.IOException;

public abstract class FilterCriterion {
    public boolean check(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        return check_(propertyOwner, propertyManager);
    }

    protected abstract boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException;
}
