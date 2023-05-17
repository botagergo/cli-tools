package task_manager.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import task_manager.data.property.PropertyDescriptor;
import task_manager.data.property.PropertyDescriptorCollection;

public class JsonPropertyDescriptorRepository implements PropertyDescriptorRepository {

    @Inject
    public JsonPropertyDescriptorRepository(@Named("basePath") File basePath) {
        final String jsonFileName = "property_descriptors.json";
        this.basePath = basePath;
        this.jsonFile = new File(basePath, jsonFileName);
    }

    @Override
    public PropertyDescriptorCollection getAll() throws IOException {
        List<HashMap<String, Object>> propertyDescriptorMaps = getPropertyDescriptors();
        PropertyDescriptorCollection propertyDescriptors = new PropertyDescriptorCollection();

        for (HashMap<String, Object> propertyDescriptor : propertyDescriptorMaps) {
            String name = (String) propertyDescriptor.get("name");
            String typeStr = (String) propertyDescriptor.get("type");
            PropertyDescriptor.Type type = switch (typeStr) {
                case "String" -> PropertyDescriptor.Type.String;
                case "Boolean" -> PropertyDescriptor.Type.Boolean;
                case "UUID" -> PropertyDescriptor.Type.UUID;
                default -> null;
            };

            propertyDescriptors.addPropertyDescriptor(
                new PropertyDescriptor(name, type, (boolean) propertyDescriptor.get("isList"),
                    propertyDescriptor.get("defaultValue")));
        }

        return propertyDescriptors;
    }

    @Override
    public void create(PropertyDescriptor propertyDescriptor) throws IOException {
        List<HashMap<String, Object>> propertyDescriptorMaps = getPropertyDescriptors();
        HashMap<String, Object> propertyDescriptorMap = new HashMap<>();
        propertyDescriptorMap.put("name", propertyDescriptor.name());
        propertyDescriptorMap.put("type", propertyDescriptor.type().toString());
        propertyDescriptorMap.put("isList", propertyDescriptor.isList());
        propertyDescriptorMap.put("defaultValue", propertyDescriptor.defaultValue());
        propertyDescriptorMaps.add(propertyDescriptorMap);
        JsonMapper.writeJson(jsonFile, propertyDescriptorMaps);
    }

    public List<HashMap<String, Object>> getPropertyDescriptors() throws IOException {
        if (!basePath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            basePath.mkdirs();
        }

        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        if (propertyDescriptors == null) {
            propertyDescriptors = JsonMapper.readJson(jsonFile);
        }
        return propertyDescriptors;
    }

    private List<HashMap<String, Object>> propertyDescriptors = null;

    private final File basePath;
    private final File jsonFile;

}
