package task_manager.logic.use_case.view;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.data.ViewInfo;
import task_manager.core.repository.ViewInfoRepository;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class ViewInfoUseCaseImpl implements ViewInfoUseCase {

    @Override
    public ViewInfo getViewInfo(String name)
            throws IOException {
        return viewInfoRepository.get(name);
    }

    @Override
    public void addViewInfo(ViewInfo viewInfo) throws IOException {
        viewInfoRepository.create(viewInfo);
    }

    @Override
    public void deleteAllViewInfos() throws IOException {
        viewInfoRepository.deleteAll();
    }

    private final ViewInfoRepository viewInfoRepository;

}
