package cli_tools.common.filter;

import cli_tools.common.property_lib.PropertyException;
import cli_tools.common.property_lib.PropertyManager;
import cli_tools.common.property_lib.PropertyOwner;

import java.io.IOException;

public class NotFilterCriterion extends FilterCriterion {

    private final FilterCriterion filterCriterion;

    public NotFilterCriterion(FilterCriterion filterCriterion) {
        this.filterCriterion = filterCriterion;
    }

    @Override
    protected boolean check_(PropertyOwner propertyOwner, PropertyManager propertyManager) throws PropertyException, IOException {
        return !filterCriterion.check_(propertyOwner, propertyManager);
    }
}
