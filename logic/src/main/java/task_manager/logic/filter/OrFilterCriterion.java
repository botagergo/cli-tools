package task_manager.logic.filter;

import lombok.Getter;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.core.property.PropertyOwner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class OrFilterCriterion extends FilterCriterion {

    public OrFilterCriterion(Collection<FilterCriterion> criteria) {
        this.criteria = criteria;
    }

    public OrFilterCriterion(FilterCriterion... criteria) {
        this.criteria = List.of(criteria);
    }

    @Getter final Collection<FilterCriterion> criteria;

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
