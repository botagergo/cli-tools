package task_manager.filter;

import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;

import java.io.IOException;

public abstract class FilterCriterion {
    public boolean check(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        return check_(propertyOwner, propertyManager);
    }

    protected abstract boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException;
}
