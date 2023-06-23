package task_manager.core.property;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import task_manager.core.repository.PropertyDescriptorRepository;

import java.io.IOException;
import java.util.*;

@Log4j2
public class PropertyManager {

    @Inject
    public PropertyManager(PropertyDescriptorRepository propertyDescriptorRepository) {
        this.propertyDescriptorRepository = propertyDescriptorRepository;
    }

    public Property getProperty(String propertyName, PropertyOwner propertyOwner)
            throws PropertyException, IOException {
        log.debug("getProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        return getProperty(propertyOwner, propertyDescriptor);
    }

    public Property getProperty(PropertyOwner propertyOwner, PropertyDescriptor propertyDescriptor)
            throws PropertyException {
        Object propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        return Property.from(propertyDescriptor, propertyValue);
    }

    public void setProperty(PropertyOwner propertyOwner, String propertyName, Object propertyValue)
            throws PropertyException, IOException {
        log.debug("setProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        Property property = Property.from(propertyDescriptor, propertyValue);

        propertyOwner.getProperties().put(propertyName, property.getValue());
    }

    public void addProperty(PropertyOwner propertyOwner, String propertyName, Collection<Object> propertyValue) throws PropertyException, IOException {
        log.debug("setProperty - {}", propertyName);

        Property origProperty = getProperty(propertyName, propertyOwner);
        Object newProperty;

        if (origProperty.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            List<Object> origList = origProperty.getList();
            newProperty = Lists.newArrayList(Iterables.concat(origList, propertyValue));
        } else if (origProperty.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            Set<Object> origSet = origProperty.getSet();
            newProperty = Sets.newLinkedHashSet(Iterables.concat(origSet, propertyValue));
        } else {
            throw new PropertyException(PropertyException.Type.NotACollection,
                origProperty.getPropertyDescriptor().name(), origProperty.getPropertyDescriptor(), null,
                PropertyDescriptor.Type.UUID);
        }

        propertyOwner.getProperties().put(propertyName, newProperty);
    }

    public void removeProperty(PropertyOwner propertyOwner, String propertyName, Collection<Object> propertyValue) throws PropertyException, IOException {
        log.debug("removeProperty - {}", propertyName);
        Property origProperty = getProperty(propertyName, propertyOwner);
        Object newProperty;

        if (origProperty.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            ArrayList<Object> origList = origProperty.getList();
            newProperty = Lists.newArrayList(Iterables.removeAll(origList, propertyValue));
        } else if (origProperty.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            LinkedHashSet<Object> origSet = origProperty.getSet();
            Iterables.removeAll(origSet, propertyValue);
            newProperty = origSet;
        } else {
            throw new PropertyException(PropertyException.Type.NotACollection,
                    origProperty.getPropertyDescriptor().name(), origProperty.getPropertyDescriptor(), null,
                    PropertyDescriptor.Type.UUID);
        }

        propertyOwner.getProperties().put(propertyName, newProperty);
    }

    public boolean hasProperty(PropertyOwner propertyOwner, String propertyName) {
        return propertyOwner.getProperties().containsKey(propertyName);
    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) throws PropertyException, IOException {
        PropertyDescriptor propertyDescriptor =
                propertyDescriptorRepository.get(propertyName);
        if (propertyDescriptor == null) {
            throw new PropertyException(PropertyException.Type.NotExist, propertyName, null, null,
                    null);
        }
        return propertyDescriptor;
    }

    private Object getPropertyValue(PropertyOwner propertyOwner,
            PropertyDescriptor propertyDescriptor) {
        Object propertyValue = propertyOwner.getProperties().get(propertyDescriptor.name());
        if (propertyValue == null) {
            log.debug("Property '{}' not set, getting default value", propertyDescriptor.name());
            propertyValue = propertyDescriptor.defaultValue();
        }
        return propertyValue;
    }

    private final PropertyDescriptorRepository propertyDescriptorRepository;

}
