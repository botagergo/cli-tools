package cli_tools.common.backend.property_descriptor.service;

import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PropertyException;

import java.io.IOException;
import java.util.List;

public interface PropertyDescriptorService {
    void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) throws IOException;

    PropertyDescriptor findPropertyDescriptor(String name) throws PropertyException, IOException;

    List<PropertyDescriptor> getPropertyDescriptors() throws IOException;
}
