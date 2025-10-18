package cli_tools.common.backend;

import cli_tools.common.backend.property_descriptor.repository.SubtypeMixIn;
import cli_tools.common.backend.repository.ObjectDeserializer;
import cli_tools.common.backend.repository.ObjectSerializer;
import cli_tools.common.backend.temp_id_mapping.TempIDManager;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapperHelper {

    private final ObjectReader propertyDescriptorObjectReader;
    private final ObjectWriter propertyDescriptorObjectWriter;

    private final ObjectReader defaultValueObjectReader;
    private final ObjectWriter defaultValueObjectWriter;

    public ObjectMapperHelper(Class<?> pseudoPropertyProviderMixInCls) {
        ObjectMapper propertyDescriptorObjectMapper = new ObjectMapper();
        propertyDescriptorObjectMapper.addMixIn(PropertyDescriptor.Subtype.class, SubtypeMixIn.class);
        propertyDescriptorObjectMapper.addMixIn(PseudoPropertyProvider.class, pseudoPropertyProviderMixInCls);
        InjectableValues injectableValues = new InjectableValues.Std()
                .addValue(TempIDManager.class, new TempIDManager());
        propertyDescriptorObjectMapper.setInjectableValues(injectableValues);
        propertyDescriptorObjectReader = propertyDescriptorObjectMapper.reader();
        propertyDescriptorObjectWriter = propertyDescriptorObjectMapper.writer();

        ObjectMapper defaultValueObjectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Object.class, new ObjectSerializer());
        module.addDeserializer(Object.class, new ObjectDeserializer());
        defaultValueObjectMapper.registerModule(module);
        defaultValueObjectReader = defaultValueObjectMapper.readerFor(Object.class);
        defaultValueObjectWriter = defaultValueObjectMapper.writerFor(Object.class);
    }

    public PropertyDescriptor.Subtype readSubtype(String subtypeJson) throws DataAccessException {
        try {
            return propertyDescriptorObjectReader.forType(PropertyDescriptor.Subtype.class).readValue(subtypeJson);
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Failed to read Subtype from JSON: %s".formatted(subtypeJson), e);
        }
    }

    public String writeSubtype(PropertyDescriptor.Subtype subtype) throws DataAccessException {
        try {
            return propertyDescriptorObjectWriter.forType(PropertyDescriptor.Subtype.class).writeValueAsString(subtype);
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Failed to write Subtype to JSON: %s".formatted(subtype), e);
        }
    }

    public PseudoPropertyProvider readPseudoPropertyProvider(String pseudoPropertyProviderJson) throws DataAccessException {
        try {
            return propertyDescriptorObjectReader.forType(PseudoPropertyProvider.class).readValue(pseudoPropertyProviderJson);
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Failed to read PseudoPropertyProvider from JSON: %s".formatted(pseudoPropertyProviderJson), e);
        }
    }

    public String writePseudoPropertyProvider(PseudoPropertyProvider pseudoPropertyProvider) throws DataAccessException {
        try {
            return propertyDescriptorObjectWriter.forType(Object.class).writeValueAsString(pseudoPropertyProvider);
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Failed to write Object to JSON: %s".formatted(pseudoPropertyProvider), e);
        }
    }

    public Object readObject(String objectJson) throws DataAccessException {
        try {
            return defaultValueObjectReader.readValue(objectJson);
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Failed to read Object from JSON: %s".formatted(objectJson), e);
        }
    }

    public String writeObject(Object object) throws DataAccessException {
        try {
            return defaultValueObjectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new DataAccessException("Failed to write Object to JSON: %s".formatted(object), e);
        }
    }

}
