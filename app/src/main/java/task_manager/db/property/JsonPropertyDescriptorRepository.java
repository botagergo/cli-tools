package task_manager.db.property;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import task_manager.db.JsonRepository;

public class JsonPropertyDescriptorRepository extends JsonRepository
        implements PropertyDescriptorRepository {

    public JsonPropertyDescriptorRepository(File basePath) {
        super(new File(basePath, jsonFileName));
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String name) throws IOException {
        List<HashMap<String, Object>> propertyDescriptors = readJson();
        Optional<HashMap<String, Object>> propertyDescriptor =
                propertyDescriptors.stream().filter(t -> t.get("name").equals(name)).findAny();

        if (!propertyDescriptor.isPresent()) {
            return null;
        }

        String typeStr = (String) propertyDescriptor.get().get("type");
        PropertyDescriptor.Type type = null;
        if (typeStr.equals("String")) {
            type = PropertyDescriptor.Type.String;
        } else if (typeStr.equals("Boolean")) {
            type = PropertyDescriptor.Type.Boolean;
        } else if (typeStr.equals("UUID")) {
            type = PropertyDescriptor.Type.UUID;
        }

        return new PropertyDescriptor((String) propertyDescriptor.get().get("name"), type,
                (boolean) propertyDescriptor.get().get("is_list"),
                propertyDescriptor.get().get("default_value"));

    }

    @Override
    public PropertyDescriptorCollection getPropertyDescriptors() throws IOException {
        List<HashMap<String, Object>> propertyDescriptorMaps = readJson();
        PropertyDescriptorCollection propertyDescriptors = new PropertyDescriptorCollection();

        for (HashMap<String, Object> propertyDescriptor : propertyDescriptorMaps) {
            String name = (String) propertyDescriptor.get("name");
            String typeStr = (String) propertyDescriptor.get("type");
            PropertyDescriptor.Type type = null;
            if (typeStr.equals("String")) {
                type = PropertyDescriptor.Type.String;
            } else if (typeStr.equals("Boolean")) {
                type = PropertyDescriptor.Type.Boolean;
            } else if (typeStr.equals("UUID")) {
                type = PropertyDescriptor.Type.UUID;
            }

            propertyDescriptors.addPropertyDescriptor(
                    new PropertyDescriptor(name, type, (boolean) propertyDescriptor.get("is_list"),
                            propertyDescriptor.get("default_value")));
        }

        return propertyDescriptors;
    }

    private static String jsonFileName = "property_descriptors.json";

}
