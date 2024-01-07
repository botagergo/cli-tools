package task_manager.logic.use_case.view;

import task_manager.core.data.ViewInfo;

import java.io.IOException;

public interface ViewInfoUseCase {
    ViewInfo getViewInfo(String name) throws IOException;
}
