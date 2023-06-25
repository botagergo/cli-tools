package task_manager;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.repository.MapDeserializer;
import task_manager.repository.MapSerializer;

import java.util.*;

import static org.testng.Assert.assertEquals;

public class MapDeserializerTest {

    @BeforeClass
    public void setup() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new MapSerializer());
        module.addDeserializer(HashMap.class, new MapDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void test_serializer_empty() throws JsonProcessingException {
        assertJsonStrEquals("{}", Map.of());
    }

    @Test
    public void test_deserializer_singleFields() throws JsonProcessingException {
        assertJsonStrEquals(String.format("""
                {
                    "text": "s:test",
                    "done": false,
                    "uuid": "u:%s",
                    "priority": 2
                }
                """, uuid1),
                new HashMap<>(Map.of("text", "test", "done", false, "uuid", uuid1, "priority", 2)));
    }

    @Test
    public void test_serializer_nullField() throws JsonProcessingException {
        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("text", null);
        expectedMap.put("uuid", null);
        assertJsonStrEquals("{\"text\":null,\"uuid\":null}", expectedMap);
    }

    @Test
    public void test_deserializer_emptyString() throws JsonProcessingException {
        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("text", "");
        assertJsonStrEquals("{\"text\":\"s:\"}", expectedMap);
    }

    @Test
    public void test_deserializer_emptyList() throws JsonProcessingException {
        assertJsonStrEquals("{\"string_list\":{\"type\":\"list\",\"value\":[]}, \"tags\":{\"type\":\"list\",\"value\":[]}}",
                new HashMap<>(Map.of("string_list", List.of(), "tags", List.of())));

    }

    @Test
    public void test_deserializer_list() throws JsonProcessingException {
        assertJsonStrEquals("{\"string_list\":{\"type\":\"list\",\"value\":[\"s:value1\",\"s:value2\"]}, \"tags\":{\"type\":\"list\",\"value\":[\"u:" + uuid1 + "\",\"u:" + uuid2 + "\"]}}",
                new HashMap<>(Map.of("string_list", List.of("value1", "value2"), "tags", List.of(uuid1, uuid2))));
    }

    @Test
    public void test_deserializer_listWithNull() throws JsonProcessingException {
        assertJsonStrEquals("{\"string_list\":{\"type\":\"list\",\"value\":[\"s:value1\", null, \"s:value2\"]},\"tags\":{\"type\":\"list\",\"value\":[\"u:" + uuid1 + "\",null,\"u:" + uuid2 + "\"]}}",
                new HashMap<>(Map.of("string_list", Lists.newArrayList("value1", null, "value2"), "tags", Lists.newArrayList(uuid1, null, uuid2))));
    }

    @Test
    public void test_deserializer_emptySet() throws JsonProcessingException {
        HashMap<String, Object> expectedMap = new HashMap<>(Map.of("string_set", Set.of(),"tags", Set.of()));
        assertJsonStrEquals("{\"string_set\":{\"type\":\"set\",\"value\":[]},\"tags\":{\"type\":\"set\",\"value\":[]}}", expectedMap);
    }

    @Test
    public void test_deserializer_set() throws JsonProcessingException {
        HashMap<String, Object> expectedMap = new HashMap<>(Map.of(
                "string_set", new LinkedHashSet<>(List.of("value1", "value2")),
                "tags", new LinkedHashSet<>(List.of(uuid1, uuid2))));
        assertJsonStrEquals("{\"string_set\":{\"type\":\"set\",\"value\":[\"s:value1\",\"s:value2\"]},\"tags\":{\"type\":\"set\",\"value\":[\"u:" + uuid1 + "\",\"u:" + uuid2 + "\"]}}", expectedMap);
    }

    @Test
    public void test_deserializer_setWithNull() throws JsonProcessingException {
        HashMap<String, Object> expectedMap = new HashMap<>(Map.of(
                "string_set", new LinkedHashSet<>(Lists.newArrayList(null,"value1", "value2")),
                "tags", new LinkedHashSet<>(Lists.newArrayList(null, uuid1, uuid2))));
        assertJsonStrEquals("{\"string_set\":{\"type\":\"set\", \"value\":[null,\"s:value1\",\"s:value2\"]},\"tags\":{\"type\":\"set\", \"value\":[null,\"u:" + uuid1 + "\",\"u:" + uuid2 + "\"]}}", expectedMap);
    }

    private void assertJsonStrEquals(String jsonStr, Map<String, Object> expectedMap) throws JsonProcessingException {
        HashMap<String, Object> parsedMap = objectMapper.readValue(jsonStr, new TypeReference<>() {});
        assertEquals(parsedMap, expectedMap);
    }

    private ObjectMapper objectMapper;
    RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();

}
