package cli_tools.common.view.service;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import cli_tools.common.core.data.ViewInfo;
import cli_tools.common.core.repository.ViewInfoRepository;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class ViewInfoServiceImpl implements ViewInfoService {

    @Override
    public ViewInfo getViewInfo(String name)
            throws IOException {
        return viewInfoRepository.get(name);
    }

    @Override
    public void addViewInfo(String name, ViewInfo viewInfo) throws IOException {
        viewInfoRepository.create(name, viewInfo);
    }

    @Override
    public void deleteAllViewInfos() throws IOException {
        viewInfoRepository.deleteAll();
    }

    private final ViewInfoRepository viewInfoRepository;

}
