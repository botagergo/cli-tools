package task_manager.api.filter;

import java.util.ArrayList;
import java.util.List;
import task_manager.db.property.PropertyException;
import task_manager.db.property.PropertyOwner;

public class SimpleFilter implements Filter {

    public SimpleFilter(FilterCriterion filterCriterion) {
        this.filterCriterion = filterCriterion;
    }

    FilterCriterion filterCriterion;

    @Override
    public <T extends PropertyOwner> List<T> doFilter(List<T> propertyOwners) throws PropertyException {
        ArrayList<T> filteredPropertyOwners = new ArrayList<>();
        for (T propertyOwner : propertyOwners) {
            if(filterCriterion.check(propertyOwner)) {
                filteredPropertyOwners.add(propertyOwner);
            }
        }
        return filteredPropertyOwners;
    }

}
