package cli_tools.common.backend.view.service;

import cli_tools.common.core.data.ViewInfo;
import cli_tools.common.core.repository.ViewInfoRepository;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class ViewInfoServiceImpl implements ViewInfoService {

    private final ViewInfoRepository viewInfoRepository;

    @Override
    public ViewInfo getViewInfo(String name) {
        return viewInfoRepository.get(name);
    }

    @Override
    public void addViewInfo(String name, ViewInfo viewInfo) {
        viewInfoRepository.create(name, viewInfo);
    }

    @Override
    public void deleteAllViewInfos() {
        viewInfoRepository.deleteAll();
    }

}
