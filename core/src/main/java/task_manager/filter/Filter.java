package task_manager.filter;

import java.io.IOException;
import java.util.List;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;
import task_manager.property.PropertyOwner;

public interface Filter {
    <T extends PropertyOwner> List<T> doFilter(List<T> propertyOwners, PropertyManager propertyManager)
            throws PropertyException, IOException;
}
