package task_manager.logic.filter;

import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyManager;
import task_manager.property_lib.PropertyOwner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleFilter implements Filter {

    public SimpleFilter(FilterCriterion filterCriterion) {
        this.filterCriterion = filterCriterion;
    }

    final FilterCriterion filterCriterion;

    @Override
    public <T extends PropertyOwner> List<T> doFilter(List<T> propertyOwners, PropertyManager propertyManager)
            throws PropertyException, IOException {
        ArrayList<T> filteredPropertyOwners = new ArrayList<>();
        for (T propertyOwner : propertyOwners) {
            if (filterCriterion.check(propertyOwner, propertyManager)) {
                filteredPropertyOwners.add(propertyOwner);
            }
        }
        return filteredPropertyOwners;
    }

}
