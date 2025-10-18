package cli_tools.common.backend.view.service;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.core.data.ViewInfo;

public interface ViewInfoService {
    ViewInfo getViewInfo(String name) throws ServiceException;

    void addViewInfo(String name, ViewInfo viewInfo) throws ServiceException;

    void deleteAllViewInfos() throws ServiceException;
}
