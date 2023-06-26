package task_manager.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import task_manager.core.util.RoundRobinUUIDGenerator;
import task_manager.core.util.Utils;

import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class ObjectSerializerTest {

    @BeforeClass
    public void setup() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Object.class, new ObjectSerializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void test_serialize_valid_int() throws JsonProcessingException {
        assertEquals(objectMapper.writeValueAsString(123), "123");
    }

    @Test
    public void test_serialize_valid_boolean() throws JsonProcessingException {
        assertEquals(objectMapper.writeValueAsString(true), "true");
        assertEquals(objectMapper.writeValueAsString(false), "false");
    }

    @Test
    public void test_serialize_valid_string() throws JsonProcessingException {
        assertEquals(objectMapper.writeValueAsString(""), "\"s:\"");
        assertEquals(objectMapper.writeValueAsString("str"), "\"s:str\"");
    }

    @Test
    public void test_serialize_valid_uuid() throws JsonProcessingException {
        assertEquals(objectMapper.writeValueAsString(uuid1), "\"u:" + uuid1 + "\"");
    }

    @Test
    public void test_serialize_valid_null() throws JsonProcessingException {
        assertEquals(objectMapper.writeValueAsString(null), "null");
    }

    @Test
    public void test_serialize_valid_int_list() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newArrayList(1, 2, 3)),
                "{\"type\":\"list\",\"value\":[1,2,3]}");
    }

    @Test
    public void test_serialize_valid_string_list() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newArrayList("str1", "str2")),
                "{\"type\":\"list\",\"value\":[\"s:str1\",\"s:str2\"]}");
    }

    @Test
    public void test_serialize_valid_boolean_list() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newArrayList(true, false)),
                "{\"type\":\"list\",\"value\":[true,false]}");
    }

    @Test
    public void test_serialize_valid_uuid_list() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newArrayList(uuid1, uuid2)),
                "{\"type\":\"list\",\"value\":[\"u:" + uuid1 + "\",\"u:" + uuid2 + "\"]}");
    }

    @Test
    public void test_serialize_valid_emptyList() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newArrayList()),
                "{\"type\":\"list\",\"value\":[]}");
    }

    @Test
    public void test_serialize_valid_nullInList() throws JsonProcessingException {
        assertEquals(objectMapper.writeValueAsString(Utils.newArrayList(1, null)), "{\"type\":\"list\",\"value\":[1,null]}");
    }

    @Test
    public void test_serialize_valid_int_set() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newLinkedHashSet(1, 2, 3)),
                "{\"type\":\"set\",\"value\":[1,2,3]}");
    }

    @Test
    public void test_serialize_valid_string_set() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newLinkedHashSet("str1", "str2")),
                "{\"type\":\"set\",\"value\":[\"s:str1\",\"s:str2\"]}");
    }

    @Test
    public void test_serialize_valid_boolean_set() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newLinkedHashSet(true, false)),
                "{\"type\":\"set\",\"value\":[true,false]}");
    }

    @Test
    public void test_serialize_valid_uuid_set() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newLinkedHashSet(uuid1, uuid2)),
                "{\"type\":\"set\",\"value\":[\"u:" + uuid1 + "\",\"u:" + uuid2 + "\"]}");
    }

    @Test
    public void test_serialize_valid_emptySet() throws JsonProcessingException {
        assertEquals(
                objectMapper.writeValueAsString(Utils.newLinkedHashSet()),
                "{\"type\":\"set\",\"value\":[]}");
    }

    @Test
    public void test_serialize_valid_nullInSet() throws JsonProcessingException {
        assertEquals(objectMapper.writeValueAsString(Utils.newLinkedHashSet(1, null)), "{\"type\":\"set\",\"value\":[1,null]}");
    }


    @Test
    public void test_notSerializable_throws() {
        assertThrows(JsonMappingException.class, () -> objectMapper.writeValueAsString(3.4));
    }

    @Test
    public void test_notSerializableListItem_throws() {
        assertThrows(JsonMappingException.class, () -> objectMapper.writeValueAsString(Utils.newArrayList(1, 1.3)));
    }

    @Test
    public void test_notSerializableSetItem_throws() {
        assertThrows(JsonMappingException.class, () -> objectMapper.writeValueAsString(Utils.newLinkedHashSet(1, 1.3)));
    }

    private ObjectMapper objectMapper;
    RoundRobinUUIDGenerator uuidGenerator = new RoundRobinUUIDGenerator();
    private final UUID uuid1 = uuidGenerator.getUUID();
    private final UUID uuid2 = uuidGenerator.getUUID();

}
