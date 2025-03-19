package cli_tools.common.filter;

import lombok.Getter;
import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;

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
