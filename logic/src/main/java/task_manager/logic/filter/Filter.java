package task_manager.logic.filter;

import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.core.property.PropertyOwner;

import java.io.IOException;
import java.util.List;

public interface Filter {
    <T extends PropertyOwner> List<T> doFilter(List<T> propertyOwners, PropertyManager propertyManager)
            throws PropertyException, IOException;
}
