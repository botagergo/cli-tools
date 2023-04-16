package task_manager.api.filter;

import task_manager.db.property.PropertyException;
import task_manager.db.property.PropertyOwner;

public abstract class FilterCriterion {
    public boolean check(PropertyOwner propertyOwner) throws PropertyException {
        return check_(propertyOwner);
    }

    protected abstract boolean check_(PropertyOwner propertyOwner) throws PropertyException;
}
