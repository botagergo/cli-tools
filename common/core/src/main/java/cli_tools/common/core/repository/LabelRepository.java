package cli_tools.common.core.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface LabelRepository {

    boolean create(String labelType, String labelText) throws IOException;

    boolean exists(String labelType, String labelName) throws IOException;

    List<String> getAllWithType(String labelType) throws IOException;

    Map<String, List<String>> getAll() throws IOException;

    boolean delete(String labelType, String labelText) throws IOException;

    void deleteAll(String labelType) throws IOException;

}
