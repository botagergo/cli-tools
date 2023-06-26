package task_manager.repository.property_descriptor;

import org.testng.annotations.Test;
import task_manager.core.property.PropertyDescriptor;
import task_manager.repository.util.JsonRepositoryCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

public class JsonPropertyDescriptorRepositoryTest {

    public JsonPropertyDescriptorRepositoryTest() throws IOException {
        rc = new JsonRepositoryCreator(Files.createTempDirectory("testng"));
    }

    @Test
    public void test_read_successful() throws IOException {
        File tempFile = rc.makeTempFile("read_successful", """
        {
            "name": {
                "name":"name",
                "type":"String",
                "multiplicity":"SINGLE",
                "defaultValue":null
            },
            "other_name": {
                "name":"other_name",
                "type":"UUID",
                "multiplicity":"SET",
                "defaultValue":null
            }
        }
        """);
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertEquals(repository.getAll(), List.of(
                new PropertyDescriptor("name", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, null),
                new PropertyDescriptor("other_name", PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SET, null)
        ));
    }

    @Test
    public void test_write_successful() throws IOException {
        File tempFile = rc.makeTempFile("write_successful", """
        {
            "name": {
                "name":"name",
                "type":"String",
                "multiplicity":"SINGLE",
                "defaultValue":null
            },
            "other_name": {
                "name":"other_name",
                "type":"UUID",
                "multiplicity":"SET",
                "defaultValue":null
            }
        }
        """);
        repository = new JsonPropertyDescriptorRepository(tempFile);
        repository.create(new PropertyDescriptor("name", PropertyDescriptor.Type.String, null, PropertyDescriptor.Multiplicity.SINGLE, null));
        repository.create(new PropertyDescriptor("other_name", PropertyDescriptor.Type.UUID, null, PropertyDescriptor.Multiplicity.SET, null));

        String content = Files.readString(tempFile.toPath());
        assertEquals(content, """
        {
            "name": {
                "name":"name",
                "type":"String",
                "multiplicity":"SINGLE"
            },
            "other_name":{
                "name":"other_name",
                "type":"UUID",
                "multiplicity":"SET"
            }
        }
        """.replaceAll("\\s+", ""));
    }

    @Test
    public void test_badFormat_throwsException() throws IOException {
        File tempFile = rc.getTempFile("bad_format");

        Files.writeString(tempFile.toPath(), "[1, 2, 3]");
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("property"));

        Files.writeString(tempFile.toPath(), "\"some string\"");
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("property"));

        Files.writeString(tempFile.toPath(), "{\"name\":123}");
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    @Test
    public void test_missingField_throwsException() throws IOException {
        File tempFile = rc.makeTempFile("missing_field", """
        {
            "name": {
                "name":"name",
                "type":"String"
            }
        }
        """);
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    @Test
    public void test_extraFields_throwsException() throws IOException {
        File tempFile = rc.makeTempFile("extra_fields", """
        {
            "name": {
                "name":"name",
                "type":"String",
                "multiplicity":"SINGLE",
                "defaultValue":null,
                "extra_field":null
            }
        }
        """);
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    @Test
    public void test_wrongFieldType_throwsException() throws IOException {
        File tempFile = rc.makeTempFile("wrong_field_type", """
        {
        "name": {
        "name":"name",
        "type":123,
        "multiplicity":"SINGLE",
        "defaultValue":null
        },"other_name": {
        "name":"other_name",
        "type":"UUID",
        "multiplicity":"SET",
        "defaultValue":null
        }
        }
        """);
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    @Test
    public void test_UUIDExtra_successful() throws IOException {
        File tempFile = rc.makeTempFile("uuid_extra", """
        {
            "other_name": {
                "name":"other_name",
                "type":"UUID",
                "multiplicity":"SET",
                "extra": {
                    "@type":"UUIDExtra",
                    "labelName":"label1"
                },
                "defaultValue":null
            }
        }
        """);
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertEquals(repository.get("other_name"), new PropertyDescriptor("other_name", PropertyDescriptor.Type.UUID, new PropertyDescriptor.UUIDExtra("label1"), PropertyDescriptor.Multiplicity.SET, null));
    }

    @Test
    public void test_wrongExtraType_throws() throws IOException {
        File tempFile = rc.makeTempFile("wrong_field_type", """
        {
            "name": {
                "name":"other_name",
                "type":"UUID",
                "multiplicity":"SET",
                "extra": {
                    "@type":"StringExtra"
                },
                "defaultValue":null
            }
        }
        """);
        repository = new JsonPropertyDescriptorRepository(tempFile);
        assertThrows(IOException.class, () -> repository.get("property"));
    }

    private JsonPropertyDescriptorRepository repository;
    private final JsonRepositoryCreator rc;

}
