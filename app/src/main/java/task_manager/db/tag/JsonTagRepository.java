package task_manager.db.tag;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import task_manager.db.JsonRepository;

public class JsonTagRepository extends JsonRepository implements TagRepository {

    public JsonTagRepository(File basePath) {
        super(new File(basePath, jsonFileName));
    }

    @Override
    public Tag findTag(String name) throws IOException {
        List<Map<String, Object>> tags = readJson();
        Optional<Map<String, Object>> tag =
                tags.stream().filter(t -> t.get("name").equals(name)).findAny();
        if (tag.isPresent()) {
            try {
                return new Tag(UUID.fromString((String) tag.get().get("uuid")), name);
            } catch (IllegalArgumentException e) {
                throw new IOException();
            }
        } else {
            return null;
        }
    }

    @Override
    public Tag getTag(UUID uuid) throws IOException {
        List<Map<String, Object>> tags = readJson();
        Optional<Map<String, Object>> tag =
                tags.stream().filter(t -> t.get("uuid").equals(uuid.toString())).findAny();
        if (tag.isPresent()) {
            return new Tag(uuid, (String) tag.get().get("name"));
        } else {
            return null;
        }
    }

    @Override
    public Tag addTag(String name) throws IOException {
        List<Map<String, Object>> tags = readJson();
        Tag tag = new Tag(UUID.randomUUID(), name);
        tags.add(Map.of("uuid", tag.getUuid().toString(), "name", name));
        writeJson(tags);
        return tag;
    }

    @Override
    public void deleteAllTags() throws IOException {
        writeJson(List.of());
    }

    private static String jsonFileName = "tags.json";

}
