package cli_tools.common.service;

import cli_tools.common.repository.ObjectDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import cli_tools.common.util.RoundRobinUUIDGenerator;
import cli_tools.common.util.Utils;

import java.util.UUID;

import static org.testng.Assert.*;

public class ObjectDeserializerTest {

    @BeforeClass
    public void setup() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Object.class, new ObjectDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void test_deserialize_valid_int() throws JsonProcessingException {
        assertEquals(objectMapper.readValue("123", Object.class), 123);
    }

    @Test
    public void test_deserialize_valid_boolean() throws JsonProcessingException {
        assertEquals(objectMapper.readValue("true", Object.class), true);
        assertEquals(objectMapper.readValue("false", Object.class), false);
    }

    @Test
    public void test_deserialize_valid_string() throws JsonProcessingException {
        assertEquals(objectMapper.readValue("\"s:\"", Object.class), "");
        assertEquals(objectMapper.readValue("\"s:str\"", Object.class), "str");
        assertEquals(objectMapper.readValue("\"\"", Object.class), "");
        assertEquals(objectMapper.readValue("\"ustr\"", Object.class), "ustr");
        assertEquals(objectMapper.readValue("\"s\"", Object.class), "s");
    }

    @Test
    public void test_deserialize_valid_emptyString() throws JsonProcessingException {
        assertEquals(objectMapper.readValue("\"s:\"", Object.class), "");
        assertEquals(objectMapper.readValue("\"s:str\"", Object.class), "str");
    }

    @Test
    public void test_deserialize_valid_uuid() throws JsonProcessingException {
        assertEquals(objectMapper.readValue("\"u:" + uuid1.toString() + "\"", Object.class), uuid1);
    }

    @Test
    public void test_deserialize_valid_null() throws JsonProcessingException {
        assertNull(objectMapper.readValue("null", Object.class));
    }

    @Test
    public void test_deserialize_valid_int_list() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"list\",\"value\":[1,2,3]}", Object.class),
                Utils.newArrayList(1, 2, 3));
    }

    @Test
    public void test_deserialize_valid_string_list() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"list\",\"value\":[\"s:str1\",\"s:str2\"]}", Object.class),
                Utils.newArrayList("str1", "str2"));
    }

    @Test
    public void test_deserialize_valid_boolean_list() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"list\",\"value\":[true,false]}", Object.class),
                Utils.newArrayList(true, false));
    }

    @Test
    public void test_deserialize_valid_uuid_list() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"list\",\"value\":[\"u:" + uuid1 + "\",\"u:" + uuid2 + "\"]}", Object.class),
                Utils.newArrayList(uuid1, uuid2));
    }

    @Test
    public void test_deserialize_valid_emptyList() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"list\",\"value\":[]}", Object.class),
                Utils.newArrayList());
    }

    @Test
    public void test_deserialize_valid_nullInList() throws JsonProcessingException {
        assertEquals(objectMapper.readValue("{\"type\":\"list\",\"value\":[1,null]}", Object.class),
                Utils.newArrayList(1, null));
    }

    @Test
    public void test_deserialize_valid_int_set() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"set\",\"value\":[1,2,3]}", Object.class),
                Utils.newLinkedHashSet(1, 2, 3));
    }

    @Test
    public void test_deserialize_valid_string_set() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"set\",\"value\":[\"s:str1\",\"s:str2\"]}", Object.class),
                Utils.newLinkedHashSet("str1", "str2"));
    }

    @Test
    public void test_deserialize_valid_boolean_set() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"set\",\"value\":[true,false]}", Object.class),
                Utils.newLinkedHashSet(true, false));
    }

    @Test
    public void test_deserialize_valid_uuid_set() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"set\",\"value\":[\"u:" + uuid1 + "\",\"u:" + uuid2 + "\"]}", Object.class),
                Utils.newLinkedHashSet(uuid1, uuid2));
    }

    @Test
    public void test_deserialize_valid_emptySet() throws JsonProcessingException {
        assertEquals(
                objectMapper.readValue("{\"type\":\"set\",\"value\":[]}", Object.class),
                Utils.newLinkedHashSet());
    }

    @Test
    public void test_deserialize_valid_nullInSet() throws JsonProcessingException {
        assertEquals(objectMapper.readValue("{\"type\":\"set\",\"value\":[1,null]}", Object.class),
                Utils.newLinkedHashSet(1, null));
    }


    @Test
    public void test_deserialize_notDeserializable_throws() {
        assertThrows(JsonMappingException.class, () -> objectMapper.readValue("3.4", Object.class));
    }

    @Test
    public void test_deserialize_notDeserializableListItem_throws() {
        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("{\"type\":\"list\",\"value\":[1,1.3]}", Object.class));
    }

    @Test
    public void test_deserialize_notDeserializableSetItem_throws() {
        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("{\"type\":\"set\",\"value\":[1,1.3]}", Object.class));
    }

    @Test
    public void test_deserialize_missingCollectionType_throws() {
        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("{\"value\":[1,1.3]}", Object.class));
    }

    @Test
    public void test_deserialize_missingCollectionValue_throws() {
        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("{\"type\":false}", Object.class));
    }

    @Test
    public void test_deserialize_invalidCollectionValue_throws() {
        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("{\"value\":123}", Object.class));
    }

    @Test
    public void test_deserialize_invalidCollectionType_throws() {
        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("{\"type\":false,\"value\":[1,1.3]}", Object.class));
        assertThrows(JsonMappingException.class, () ->
                objectMapper.readValue("{\"type\":\"str\",\"value\":[1,1.3]}", Object.class));
    }

    private ObjectMapper objectMapper;
    final RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();

}
