package cli_tools.common.backend.property_descriptor.service;

import cli_tools.common.backend.service.ServiceException;
import cli_tools.common.property_lib.PropertyDescriptor;

import java.util.List;

public interface PropertyDescriptorService {
    void createPropertyDescriptor(PropertyDescriptor propertyDescriptor) throws ServiceException;

    PropertyDescriptor findPropertyDescriptor(String name, boolean allowPrefix) throws ServiceException;

    List<PropertyDescriptor> getPropertyDescriptors() throws ServiceException;
}
