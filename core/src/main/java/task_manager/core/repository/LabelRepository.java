package task_manager.core.repository;

import task_manager.core.data.Label;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface LabelRepository {

    Label create(Label label) throws IOException;
    Label get(UUID uuid) throws IOException;
    List<Label> getAll() throws IOException;
    Label find(String name) throws IOException;
    Label update(Label label) throws IOException;
    boolean delete(UUID uuid) throws IOException;
    void deleteAll() throws IOException;

}
