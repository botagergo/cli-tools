package cli_tools.common.service;

import cli_tools.common.repository.MapDeserializer;
import cli_tools.common.repository.MapSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Sets;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class MapSerializerTest {

    @BeforeClass
    public void setup() {
        objectMapper = new ObjectMapper();
        basicObjectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new MapSerializer());
        module.addDeserializer(HashMap.class, new MapDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void test_serializer_empty() throws JsonProcessingException {
        assertJsonStrEquals(objectMapper.writeValueAsString(Map.of()), Map.of());
    }

    @Test
    public void test_serializer_singleFields() throws JsonProcessingException {
        assertJsonStrEquals(objectMapper.writeValueAsString(
                        new HashMap<>(Map.of("name", "test", "done", false, "uuid", uuid1, "priority", 2))),
                new HashMap<>(Map.of("name", "s:test", "done", false, "uuid", "u:" + uuid1, "priority", 2)));
    }

    @Test
    public void test_serializer_nullField() throws JsonProcessingException {
        HashMap<String, Object> mapToSerialize = new HashMap<>(), expectedMap = new HashMap<>();
        mapToSerialize.put("name", null);
        expectedMap.put("name", null);
        assertJsonStrEquals(objectMapper.writeValueAsString(mapToSerialize), expectedMap);
    }

    @Test
    public void test_serializer_emptyString() throws JsonProcessingException {
        assertJsonStrEquals(objectMapper.writeValueAsString(
                new HashMap<>(Map.of("name", ""))),
                new HashMap<>(Map.of("name", "s:")));
    }

    @Test
    public void test_serializer_emptyList() throws JsonProcessingException {
        assertJsonStrEquals(objectMapper.writeValueAsString(
                new HashMap<>(Map.of("string_list", new ArrayList<>(), "tags", new ArrayList<>()))),
                new HashMap<>(Map.of(
                        "string_list", Map.of("type", "list", "value", new ArrayList<>()),
                        "tags", Map.of("type", "list", "value", new ArrayList<>()))));
    }

    @Test
    public void test_serializer_list() throws JsonProcessingException {
        assertJsonStrEquals(objectMapper.writeValueAsString(
                new HashMap<>(Map.of("string_list", Utils.newArrayList("value1", "value2"), "tags", Utils.newArrayList(uuid1, uuid2)))),
                new HashMap<>(Map.of(
                        "string_list", Map.of("type", "list", "value", Utils.newArrayList("s:value1", "s:value2")),
                        "tags", Map.of("type", "list", "value", Utils.newArrayList("u:" + uuid1, "u:" + uuid2)))));
    }

    @Test
    public void test_serializer_listWithNull() throws JsonProcessingException {
        assertJsonStrEquals(objectMapper.writeValueAsString(
                        new HashMap<>(Map.of("string_list", Utils.newArrayList("value1", null, "value2"), "tags", Utils.newArrayList(uuid1, null, uuid2)))),
                new HashMap<>(Map.of(
                        "string_list", Map.of("type", "list", "value", Utils.newArrayList("s:value1", null, "s:value2")),
                        "tags", Map.of("type", "list", "value", Utils.newArrayList("u:" + uuid1, null, "u:" + uuid2)))));
    }

    @Test
    public void test_serializer_emptySet() throws JsonProcessingException {
        HashMap<String, Object> mapToSerialize = new HashMap<>(Map.of("string_set", Sets.newLinkedHashSet(), "tags", Sets.newLinkedHashSet()));
        HashMap<String, Object> expectedMap = new HashMap<>(Map.of(
                "string_set", Map.of("type", "set", "value", new ArrayList<>()),
                "tags", Map.of("type", "set", "value", new ArrayList<>())));
        assertJsonStrEquals(objectMapper.writeValueAsString(mapToSerialize), expectedMap);
    }

    @Test
    public void test_serializer_set() throws JsonProcessingException {
        HashMap<String, Object> mapToSerialize = new HashMap<>(Map.of("string_set", Sets.newLinkedHashSet(Utils.newArrayList("value2", "value1")), "tags", Sets.newLinkedHashSet(Utils.newArrayList(uuid1, uuid2))));
        String jsonStr = objectMapper.writeValueAsString(mapToSerialize);
        HashMap<String, Object> expectedMap = new HashMap<>(Map.of(
                "string_set", Map.of("type", "set", "value", Utils.newArrayList("s:value2", "s:value1")),
                "tags", Map.of("type", "set", "value", Utils.newArrayList("u:" + uuid1, "u:" + uuid2))));
        assertJsonStrEquals(jsonStr, expectedMap);
    }

    @Test
    public void test_serializer_setWithNull() throws JsonProcessingException {
        assertJsonStrEquals(objectMapper.writeValueAsString(
                new HashMap<>(Map.of("string_set", Sets.newLinkedHashSet(Utils.newArrayList("value1", null, "value2")), "tags", Sets.newLinkedHashSet(Utils.newArrayList(uuid1, null, uuid2))))),
                new HashMap<>(Map.of(
                        "string_set", Map.of("type", "set", "value", Utils.newArrayList("s:value1", null, "s:value2")),
                        "tags", Map.of("type", "set", "value", Utils.newArrayList("u:" + uuid1, null, "u:" + uuid2)))));
    }

    private void assertJsonStrEquals(String jsonStr, Map<String, Object> map) throws JsonProcessingException {
        Map<String, Object> parsedMap = basicObjectMapper.readValue(jsonStr, new TypeReference<>() {});
        assertEquals(parsedMap, map);
    }

    private ObjectMapper objectMapper;
    private ObjectMapper basicObjectMapper;
    final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();

}
