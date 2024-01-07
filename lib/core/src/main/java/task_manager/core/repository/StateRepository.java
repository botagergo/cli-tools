package task_manager.core.repository;

import java.io.IOException;

public interface StateRepository {

    Object getValue(String name) throws IOException;

}
