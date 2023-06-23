package task_manager.logic.use_case.view;

import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.logic.filter.FilterCriterionException;

import java.io.IOException;

public interface ViewUseCase {
    View getView(String name, PropertyManager propertyManager) throws IOException, PropertyException, PropertyConverterException, FilterCriterionException;
}
