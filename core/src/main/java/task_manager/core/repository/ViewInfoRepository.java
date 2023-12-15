package task_manager.core.repository;

import task_manager.core.data.ViewInfo;

import java.io.IOException;

public interface ViewInfoRepository {

    ViewInfo create(ViewInfo viewInfo) throws IOException;
    ViewInfo get(String name) throws IOException;

}
