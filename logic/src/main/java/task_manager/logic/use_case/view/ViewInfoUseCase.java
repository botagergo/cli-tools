package task_manager.logic.use_case.view;

import task_manager.core.data.ViewInfo;
import task_manager.core.property.PropertyException;
import task_manager.core.property.PropertyManager;
import task_manager.logic.filter.FilterCriterionException;
import task_manager.logic.use_case.task.PropertyConverterException;

import java.io.IOException;

public interface ViewInfoUseCase {
    ViewInfo getViewInfo(String name, PropertyManager propertyManager) throws IOException, PropertyException, PropertyConverterException, FilterCriterionException;
}
