package cli_tools.common.core.repository;

import cli_tools.common.core.data.ViewInfo;

public interface ViewInfoRepository {

    ViewInfo get(String name) throws DataAccessException;

    void create(String name, ViewInfo viewInfo) throws DataAccessException;

    void deleteAll() throws DataAccessException;

}
