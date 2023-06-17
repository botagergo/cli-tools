package task_manager.repository;

import task_manager.data.ViewInfo;

import java.io.IOException;
import java.util.HashMap;

public interface ViewInfoRepository {

    ViewInfo create(ViewInfo viewInfo) throws IOException;
    ViewInfo get(String name) throws IOException;
    HashMap<String, ViewInfo> getAll() throws IOException;
    ViewInfo update(ViewInfo viewInfo) throws IOException;
    boolean delete(String name) throws IOException;

}
