package task_manager.logic.filter;

import lombok.Getter;
import task_manager.property_lib.PropertyException;
import task_manager.property_lib.PropertyManager;
import task_manager.property_lib.PropertyOwner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Getter
public class OrFilterCriterion extends FilterCriterion {

    public OrFilterCriterion(Collection<FilterCriterion> criteria) {
        this.criteria = criteria;
    }

    public OrFilterCriterion(FilterCriterion... criteria) {
        this.criteria = List.of(criteria);
    }

    final Collection<FilterCriterion> criteria;

    @Override
    protected boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        for (FilterCriterion criterion : criteria) {
            if (criterion.check(propertyOwner, propertyManager)) {
                return true;
            }
        }
        return false;
    }
}
