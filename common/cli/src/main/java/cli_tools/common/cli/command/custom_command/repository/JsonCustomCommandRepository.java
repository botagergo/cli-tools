package cli_tools.common.cli.command.custom_command.repository;

import cli_tools.common.cli.command.custom_command.CustomCommandDefinition;
import cli_tools.common.backend.repository.SimpleJsonRepository;
import cli_tools.common.core.repository.DataAccessException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.File;
import java.util.List;

public class JsonCustomCommandRepository extends SimpleJsonRepository<List<CustomCommandDefinition>> implements CustomCommandRepository {

    @Inject
    public JsonCustomCommandRepository(@Named("customCommandDefinitionJsonFile") File jsonFile) {
        super(jsonFile);
    }

    @Override
    public List<CustomCommandDefinition> getAll() throws DataAccessException {
        return getData();
    }

    public void setMixIn(Class<?> mixinSource) {
        getObjectMapper().addMixIn(CustomCommandDefinition.class, mixinSource);
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(List.class, CustomCommandDefinition.class);

    }

    @Override
    protected List<CustomCommandDefinition> getEmptyData() {
        return List.of();
    }

}
