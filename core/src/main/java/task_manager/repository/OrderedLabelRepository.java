package task_manager.repository;

import task_manager.data.OrderedLabel;

import java.io.IOException;
import java.util.List;

public interface OrderedLabelRepository {

    OrderedLabel create(String text) throws IOException;

    OrderedLabel get(int value) throws IOException;

    List<OrderedLabel> getAll() throws IOException;

    OrderedLabel find(String text) throws IOException;

    OrderedLabel update(OrderedLabel orderedLabel) throws IOException;

    boolean delete(int value) throws IOException;

}
