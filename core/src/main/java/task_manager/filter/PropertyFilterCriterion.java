package task_manager.filter;

import task_manager.data.property.Property;
import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyOwner;

public abstract class PropertyFilterCriterion extends FilterCriterion {

    public PropertyFilterCriterion(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean check_(PropertyOwner propertyOwner) throws PropertyException {
        Property property = propertyOwner.getProperty(propertyName);
        return check_(property);
    }

    public abstract boolean check_(Property propertyValue) throws PropertyException;

    public final String propertyName;

}
