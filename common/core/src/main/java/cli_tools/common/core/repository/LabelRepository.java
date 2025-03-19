package cli_tools.common.core.repository;

import cli_tools.common.core.data.Label;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface LabelRepository {

    Label create(Label label) throws IOException;
    Label get(UUID uuid) throws IOException;
    List<Label> getAll() throws IOException;
    Label find(String name) throws IOException;
    void deleteAll() throws IOException;

}
