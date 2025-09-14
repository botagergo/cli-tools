package cli_tools.common.core.repository;

import cli_tools.common.core.data.OrderedLabel;

import java.io.IOException;
import java.util.List;

public interface OrderedLabelRepository {

    void create(String type, String text) throws IOException;

    String get(String type, int value) throws IOException;

    List<String> getAll(String type) throws IOException;

    Integer find(String type, String text) throws IOException;

    void deleteAll(String type) throws IOException;

}
