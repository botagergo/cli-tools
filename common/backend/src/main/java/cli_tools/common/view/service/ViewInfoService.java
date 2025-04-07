package cli_tools.common.view.service;

import cli_tools.common.core.data.ViewInfo;

import java.io.IOException;

public interface ViewInfoService {
    ViewInfo getViewInfo(String name) throws IOException;
    void addViewInfo(String name, ViewInfo viewInfo) throws IOException;
    void deleteAllViewInfos() throws IOException;
}
