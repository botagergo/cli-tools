package task_manager.api.filter;

import java.util.List;
import task_manager.db.property.PropertyException;
import task_manager.db.property.PropertyOwner;

public interface Filter {
    public <T extends PropertyOwner> List<T> doFilter(List<T> propertyOwners) throws PropertyException;
}
