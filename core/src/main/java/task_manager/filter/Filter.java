package task_manager.filter;

import java.util.List;
import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyOwner;

public interface Filter {
    <T extends PropertyOwner> List<T> doFilter(List<T> propertyOwners)
        throws PropertyException;
}
