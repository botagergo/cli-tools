package task_manager.filter;

import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyOwner;

public abstract class FilterCriterion {
    public boolean check(PropertyOwner propertyOwner) throws PropertyException {
        return check_(propertyOwner);
    }

    protected abstract boolean check_(PropertyOwner propertyOwner) throws PropertyException;
}
