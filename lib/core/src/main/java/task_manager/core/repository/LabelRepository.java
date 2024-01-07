package task_manager.core.repository;

import task_manager.core.data.Label;

import java.io.IOException;
import java.util.UUID;

public interface LabelRepository {

    Label create(Label label) throws IOException;
    Label get(UUID uuid) throws IOException;

    Label find(String name) throws IOException;
    void deleteAll() throws IOException;

}
