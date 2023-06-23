package task_manager.logic.filter;

import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.core.property.PropertyOwner;

import java.io.IOException;

public abstract class FilterCriterion {
    public boolean check(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        return check_(propertyOwner, propertyManager);
    }

    protected abstract boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException;
}
