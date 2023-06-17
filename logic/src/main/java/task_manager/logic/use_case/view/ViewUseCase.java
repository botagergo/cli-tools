package task_manager.logic.use_case.view;

import task_manager.filter.FilterCriterionException;
import task_manager.property.PropertyException;
import task_manager.property.PropertyManager;

import java.io.IOException;

public interface ViewUseCase {
    View getView(String name, PropertyManager propertyManager) throws IOException, PropertyException, PropertyConverterException, FilterCriterionException;
}
