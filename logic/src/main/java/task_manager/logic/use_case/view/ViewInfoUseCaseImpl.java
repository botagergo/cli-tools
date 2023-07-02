package task_manager.logic.use_case.view;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.data.ViewInfo;
import task_manager.core.property.PropertyManager;
import task_manager.core.repository.ViewInfoRepository;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class ViewInfoUseCaseImpl implements ViewInfoUseCase {

    @Override
    public ViewInfo getViewInfo(String name, PropertyManager propertyManager)
            throws IOException {
        return viewInfoRepository.get(name);
    }

    private final ViewInfoRepository viewInfoRepository;

}
