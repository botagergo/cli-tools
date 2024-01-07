package task_manager.core.repository;

import task_manager.core.data.OrderedLabel;

import java.io.IOException;

public interface OrderedLabelRepository {

    void create(String text) throws IOException;

    OrderedLabel get(int value) throws IOException;

    OrderedLabel find(String text) throws IOException;

}
