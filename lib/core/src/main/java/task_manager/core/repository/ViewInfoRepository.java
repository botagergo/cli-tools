package task_manager.core.repository;

import task_manager.core.data.ViewInfo;

import java.io.IOException;

public interface ViewInfoRepository {

    ViewInfo get(String name) throws IOException;
    void create(ViewInfo viewInfo) throws IOException;
    void deleteAll() throws IOException;

    }
