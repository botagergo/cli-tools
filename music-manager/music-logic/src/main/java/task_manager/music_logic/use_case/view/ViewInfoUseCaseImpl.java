package common.music_logic.use_case.view;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import common.core.data.ViewInfo;
import common.core.repository.ViewInfoRepository;
import common.property_lib.PropertyManager;

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
