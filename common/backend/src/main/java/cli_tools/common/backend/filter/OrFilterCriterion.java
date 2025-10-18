package cli_tools.common.backend.filter;

import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class OrFilterCriterion extends FilterCriterion {

    final Collection<FilterCriterion> criteria;

    public OrFilterCriterion(Collection<FilterCriterion> criteria) {
        this.criteria = criteria;
    }

    public OrFilterCriterion(FilterCriterion... criteria) {
        this.criteria = List.of(criteria);
    }

    @Override
    protected boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException {
        for (FilterCriterion criterion : criteria) {
            if (criterion.check(propertyOwner, propertyManager)) {
                return true;
            }
        }
        return false;
    }
}
