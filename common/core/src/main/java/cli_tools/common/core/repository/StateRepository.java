package cli_tools.common.core.repository;

import java.io.IOException;

public interface StateRepository {

    Object getValue(String name) throws IOException;

}
