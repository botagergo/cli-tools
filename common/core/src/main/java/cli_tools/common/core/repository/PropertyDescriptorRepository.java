package cli_tools.common.core.repository;

import cli_tools.common.property_lib.PropertyDescriptor;
import lombok.NonNull;

import java.util.List;

public interface PropertyDescriptorRepository {

    void create(@NonNull PropertyDescriptor propertyDescriptor) throws DataAccessException;

    PropertyDescriptor get(@NonNull String name) throws DataAccessException;

    @NonNull List<PropertyDescriptor> find(@NonNull String name) throws DataAccessException;

    @NonNull List<PropertyDescriptor> getAll() throws DataAccessException;

}
