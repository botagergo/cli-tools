package task_manager.core.repository;

import task_manager.core.property.PropertyDescriptor;

import java.io.IOException;
import java.util.List;

public interface StateRepository {

    Object getValue(String name) throws IOException;
    void setValue(String name, Object value) throws IOException;

}
