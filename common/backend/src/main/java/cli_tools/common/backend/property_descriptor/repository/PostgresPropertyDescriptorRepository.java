package cli_tools.common.backend.property_descriptor.repository;

import cli_tools.common.backend.ObjectMapperHelper;
import cli_tools.common.backend.repository.PostgresRepository;
import cli_tools.common.core.repository.DataAccessException;
import cli_tools.common.core.repository.PropertyDescriptorRepository;
import cli_tools.common.db_schema.enums.Multiplicity;
import cli_tools.common.db_schema.enums.PropertyType;
import cli_tools.common.db_schema.tables.records.PropertyDescriptorsRecord;
import cli_tools.common.property_lib.PropertyDescriptor;
import cli_tools.common.property_lib.PseudoPropertyProvider;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static cli_tools.common.db_schema.Tables.PROPERTY_DESCRIPTORS;

@Log4j2
public class PostgresPropertyDescriptorRepository extends PostgresRepository implements PropertyDescriptorRepository {

    private final ObjectMapperHelper objectMapperHelper;

    public PostgresPropertyDescriptorRepository(DataSource dataSource, ObjectMapperHelper objectMapperHelper) {
        super(dataSource);
        this.objectMapperHelper = objectMapperHelper;
    }

    @Override
    public void create(@NonNull PropertyDescriptor propertyDescriptor) throws DataAccessException {
        var insert = ctx().insertInto(PROPERTY_DESCRIPTORS)
                .set(PROPERTY_DESCRIPTORS.NAME, propertyDescriptor.name())
                .set(PROPERTY_DESCRIPTORS.TYPE, PropertyType.valueOf(propertyDescriptor.type().name()))
                .set(PROPERTY_DESCRIPTORS.MULTIPLICITY, Multiplicity.valueOf(propertyDescriptor.multiplicity().name()));
        if (propertyDescriptor.subtype() != null) {
            insert = insert.set(PROPERTY_DESCRIPTORS.SUBTYPE, objectMapperHelper.writeSubtype(propertyDescriptor.subtype()));
        }
        if (propertyDescriptor.defaultValue() != null) {
            insert = insert.set(PROPERTY_DESCRIPTORS.DEFAULT_VALUE, objectMapperHelper.writeObject(propertyDescriptor.defaultValue()));

        }
        if (propertyDescriptor.pseudoPropertyProvider() != null) {
            insert = insert.set(PROPERTY_DESCRIPTORS.PSEUDO_PROPERTY_PROVIDER, objectMapperHelper.writePseudoPropertyProvider(propertyDescriptor.pseudoPropertyProvider()));
        }
        insert.execute();
    }

    @Override
    public PropertyDescriptor get(@NonNull String name) {
        var record = ctx().selectFrom(PROPERTY_DESCRIPTORS)
                .where(PROPERTY_DESCRIPTORS.NAME.eq(name))
                .fetchOne();
        if (record == null) {
            return null;
        }
        return recordToPropertyDescriptor(record);
    }

    @Override
    public @NonNull List<PropertyDescriptor> find(@NonNull String name) {
        List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();
        var records = ctx().selectFrom(PROPERTY_DESCRIPTORS)
                .where(PROPERTY_DESCRIPTORS.NAME.startsWith(name));
        for (var record : records) {
            propertyDescriptors.add(recordToPropertyDescriptor(record));
        }
        return propertyDescriptors;
    }

    @Override
    public @NonNull List<PropertyDescriptor> getAll() throws DataAccessException {
        List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();
        var records = ctx().selectFrom(PROPERTY_DESCRIPTORS);
        for (var record : records) {
            propertyDescriptors.add(recordToPropertyDescriptor(record));
        }
        return propertyDescriptors;
    }

    private PropertyDescriptor recordToPropertyDescriptor(PropertyDescriptorsRecord record) throws DataAccessException {
        String subtypeJson = record.get(PROPERTY_DESCRIPTORS.SUBTYPE);
        String defaultValueJson = record.get(PROPERTY_DESCRIPTORS.DEFAULT_VALUE);
        String pseudoPropertyProviderJson = record.get(PROPERTY_DESCRIPTORS.PSEUDO_PROPERTY_PROVIDER);

        PropertyDescriptor.Subtype subtype = null;
        if (subtypeJson != null) {
            subtype = objectMapperHelper.readSubtype(subtypeJson);
        }
        Object defaultValue = null;
        if (defaultValueJson != null) {
            defaultValue = objectMapperHelper.readObject(defaultValueJson);
        }
        PseudoPropertyProvider pseudoPropertyProvider = null;
        if (pseudoPropertyProviderJson != null) {
            pseudoPropertyProvider = objectMapperHelper.readPseudoPropertyProvider(pseudoPropertyProviderJson);
        }

        return new PropertyDescriptor(
                record.get(PROPERTY_DESCRIPTORS.NAME),
                PropertyDescriptor.Type.valueOf(record.get(PROPERTY_DESCRIPTORS.TYPE).name()),
                subtype,
                PropertyDescriptor.Multiplicity.valueOf(record.get(PROPERTY_DESCRIPTORS.MULTIPLICITY).name()),
                defaultValue,
                pseudoPropertyProvider);
    }

}
