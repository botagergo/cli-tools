package cli_tools.common.core.repository;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface LabelRepository {

    boolean create(String labelType, String labelText) throws IOException;

    boolean exists(String labelType, String labelName) throws IOException;

    List<String> getAll(String labelType) throws IOException;

    void deleteAll(String labelType) throws IOException;

}
