package task_manager.task_logic.use_case.view;

import task_manager.core.data.ViewInfo;
import task_manager.property_lib.PropertyManager;

import java.io.IOException;

public interface ViewInfoUseCase {
    ViewInfo getViewInfo(String name, PropertyManager propertyManager) throws IOException;
}
