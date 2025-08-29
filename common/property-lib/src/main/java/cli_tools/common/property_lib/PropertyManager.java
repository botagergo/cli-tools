package cli_tools.common.property_lib;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
@Log4j2
public class PropertyManager {

    private PropertyDescriptorCollection propertyDescriptorCollection;

    public PropertyManager() {
        this.propertyDescriptorCollection = new PropertyDescriptorCollection();
    }

    public Property getProperty(PropertyOwner propertyOwner, String propertyName)
            throws PropertyException, IOException {
        log.debug("getProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        if (propertyDescriptor == null) {
            throw new PropertyException("property '%s' does not exist".formatted(propertyName));
        }
        return getProperty(propertyOwner, propertyDescriptor);
    }

    public Property getProperty(PropertyOwner propertyOwner, PropertyDescriptor propertyDescriptor)
            throws PropertyException, IOException {
        Object propertyValue;
        if (propertyDescriptor.pseudoPropertyProvider() != null) {
            propertyValue = propertyDescriptor.pseudoPropertyProvider().getProperty(this, propertyOwner);
        } else {
            propertyValue = getPropertyValue(propertyOwner, propertyDescriptor);
        }
        return Property.from(propertyDescriptor, propertyValue);
    }

    public void setProperty(PropertyOwner propertyOwner, String propertyName, Object propertyValue)
            throws PropertyException {
        log.debug("setProperty - {}", propertyName);

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
        if (propertyDescriptor == null) {
            throw new PropertyException("property '%s' does not exist".formatted(propertyName));
        }

        Property property = Property.from(propertyDescriptor, propertyValue);

        propertyOwner.getProperties().put(propertyName, property.getValue());
    }

    public void addPropertyValues(PropertyOwner propertyOwner, String propertyName, Collection<Object> propertyValue) throws PropertyException, IOException {
        log.debug("addProperty - {}", propertyName);

        if (propertyValue == null) {
            log.debug("null collection is passed, returning");
            return;
        }

        Property origProperty = getProperty(propertyOwner, propertyName);
        Object newProperty;

        if (origProperty.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            List<Object> origList = origProperty.getList();
            if (origList == null) {
                newProperty = propertyValue;
            } else {
                newProperty = Stream.concat(origList.stream(), propertyValue.stream()).toList();
            }
        } else if (origProperty.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            Set<Object> origSet = origProperty.getSet();
            if (origSet == null) {
                newProperty = propertyValue;
            } else {
                // Disable this inspection as toUnmodifiableSet doesn't work with null values
                // noinspection FuseStreamOperations
                newProperty = Collections.unmodifiableSet(Stream.concat(origSet.stream(), propertyValue.stream()).collect(Collectors.toSet()));
            }
        } else {
            throw new PropertyException("property '%s' is not a collection".formatted(propertyName));
        }

        propertyOwner.getProperties().put(propertyName, newProperty);
    }

    public void removePropertyValues(PropertyOwner propertyOwner, String propertyName, Collection<Object> propertyValue) throws PropertyException, IOException {
        log.debug("removeProperty - {}", propertyName);

        if (propertyValue == null) {
            log.debug("null collection is passed, returning");
            return;
        }

        Property origProperty = getProperty(propertyOwner, propertyName);
        Collection<Object> newProperty;

        if (origProperty.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.LIST) {
            List<Object> origList = origProperty.getList();
            if (origList == null) {
                return;
            }
            List<Object> newList = new ArrayList<>(origList);
            Iterables.removeAll(newList, propertyValue);
            newProperty = Collections.unmodifiableList(newList);
        } else if (origProperty.getPropertyDescriptor().multiplicity() == PropertyDescriptor.Multiplicity.SET) {
            Set<Object> origSet = origProperty.getSet();
            if (origSet == null) {
                return;
            }
            Set<Object> newSet = new LinkedHashSet<>(origSet);
            Iterables.removeAll(newSet, propertyValue);
            newProperty = Collections.unmodifiableSet(newSet);
        } else {
            throw new PropertyException("property '%s' does not exist".formatted(propertyName));
        }

        propertyOwner.getProperties().put(propertyName, newProperty);
    }

    public void removeProperty(PropertyOwner propertyOwner, String propertyName) {
        propertyOwner.getProperties().remove(propertyName);
    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) throws PropertyException {
        return propertyDescriptorCollection.get(propertyName);
    }

    private Object getPropertyValue(PropertyOwner propertyOwner,
                                    PropertyDescriptor propertyDescriptor) {
        return propertyOwner.getProperties().get(propertyDescriptor.name());
    }

}
