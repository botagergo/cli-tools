package common.music_logic.use_case.view;

import common.core.data.ViewInfo;
import common.property_lib.PropertyManager;

import java.io.IOException;

public interface ViewInfoUseCase {
    ViewInfo getViewInfo(String name, PropertyManager propertyManager) throws IOException;
}
