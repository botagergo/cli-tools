package task_manager.core.repository;

import task_manager.core.data.ViewInfo;

import java.io.IOException;

public interface ViewInfoRepository {

    ViewInfo get(String name) throws IOException;

}
