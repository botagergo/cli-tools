package task_manager.repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import task_manager.property.PropertyDescriptor;
import task_manager.property.PropertyDescriptorCollection;

public class JsonPropertyDescriptorRepository implements PropertyDescriptorRepository {

    @Inject
    public JsonPropertyDescriptorRepository(@Named("basePath") File basePath) {
        final String jsonFileName = "property_descriptor.json";
        this.basePath = basePath;
        this.jsonFile = new File(basePath, jsonFileName);
    }

    @Override
    public PropertyDescriptor get(String name) throws IOException {
        HashMap<String, HashMap<String, Object>> propertyDescriptorMaps = getPropertyDescriptors();

        HashMap<String, Object> propertyDescriptorMap = propertyDescriptorMaps.getOrDefault(name, null);
        if (propertyDescriptorMap == null) {
            return null;
        }
        return mapToPropertyDescriptor(name, propertyDescriptorMap);
    }

    @Override
    public PropertyDescriptorCollection getAll() throws IOException {
        HashMap<String, HashMap<String, Object>> propertyDescriptorMaps = getPropertyDescriptors();
        PropertyDescriptorCollection propertyDescriptors = new PropertyDescriptorCollection();

        for (Map.Entry<String, HashMap<String, Object>> entry : propertyDescriptorMaps.entrySet()) {
            String name = entry.getKey();
            HashMap<String, Object> propertyDescriptorMap = entry.getValue();
            propertyDescriptors.addPropertyDescriptor(mapToPropertyDescriptor(name, propertyDescriptorMap));
        }

        return propertyDescriptors;
    }

    @Override
    public void create(PropertyDescriptor propertyDescriptor) throws IOException {
        HashMap<String, HashMap<String, Object>> propertyDescriptorMaps = getPropertyDescriptors();
        HashMap<String, Object> propertyDescriptorMap = new HashMap<>();
        propertyDescriptorMap.put("name", propertyDescriptor.name());
        propertyDescriptorMap.put("type", propertyDescriptor.type().toString());
        propertyDescriptorMap.put("multiplicity", propertyDescriptor.multiplicity().toString());
        propertyDescriptorMap.put("defaultValue", propertyDescriptor.defaultValue());
        propertyDescriptorMaps.put(propertyDescriptor.name(), propertyDescriptorMap);
        JsonMapper.writeJsonMap(jsonFile, propertyDescriptorMaps);
    }

    private PropertyDescriptor mapToPropertyDescriptor(String name, HashMap<String, Object> propertyDescriptorMap) {
        String typeStr = (String) propertyDescriptorMap.get("type");
        PropertyDescriptor.Type type = switch (typeStr) {
            case "String" -> PropertyDescriptor.Type.String;
            case "Boolean" -> PropertyDescriptor.Type.Boolean;
            case "UUID" -> PropertyDescriptor.Type.UUID;
            default -> null;
        };

        return new PropertyDescriptor(name, type, PropertyDescriptor.Multiplicity.valueOf((String) propertyDescriptorMap.get("multiplicity")),
                propertyDescriptorMap.get("defaultValue"));
    }

    private HashMap<String, HashMap<String, Object>> getPropertyDescriptors() throws IOException {
        if (propertyDescriptors != null) {
            return propertyDescriptors;

        }
        if (!basePath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            basePath.mkdirs();
        }

        if (!jsonFile.exists()) {
            return new HashMap<>();
        }

        propertyDescriptors = JsonMapper.readJsonMap(jsonFile);
        return propertyDescriptors;
    }

    private HashMap<String, HashMap<String, Object>> propertyDescriptors = null;

    private final File basePath;
    private final File jsonFile;

}
