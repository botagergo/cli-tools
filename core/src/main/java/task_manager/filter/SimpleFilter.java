package task_manager.filter;

import java.util.ArrayList;
import java.util.List;
import task_manager.data.property.PropertyException;
import task_manager.data.property.PropertyOwner;

public class SimpleFilter implements Filter {

    public SimpleFilter(FilterCriterion filterCriterion) {
        this.filterCriterion = filterCriterion;
    }

    final FilterCriterion filterCriterion;

    @Override
    public <T extends PropertyOwner> List<T> doFilter(List<T> propertyOwners)
        throws PropertyException {
        ArrayList<T> filteredPropertyOwners = new ArrayList<>();
        for (T propertyOwner : propertyOwners) {
            if (filterCriterion.check(propertyOwner)) {
                filteredPropertyOwners.add(propertyOwner);
            }
        }
        return filteredPropertyOwners;
    }

}
