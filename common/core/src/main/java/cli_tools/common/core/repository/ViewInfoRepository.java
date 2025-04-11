package cli_tools.common.core.repository;

import cli_tools.common.core.data.ViewInfo;

import java.io.IOException;

public interface ViewInfoRepository {

    ViewInfo get(String name) throws IOException;

    void create(String name, ViewInfo viewInfo) throws IOException;

    void deleteAll() throws IOException;

}
