package task_manager.filter;

import task_manager.property.Property;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;

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
