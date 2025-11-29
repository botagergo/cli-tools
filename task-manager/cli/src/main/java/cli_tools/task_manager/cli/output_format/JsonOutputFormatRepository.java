package cli_tools.task_manager.cli.output_format;

import cli_tools.common.backend.repository.SimpleJsonRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JsonOutputFormatRepository extends SimpleJsonRepository<Map<String, OutputFormat>> implements OutputFormatRepository {

    public JsonOutputFormatRepository(File jsonFile) {
        super(jsonFile);
    }

    @Override
    public OutputFormat get(String name) {
        return getData().get(name);
    }

    @Override
    public void create(String name, OutputFormat outputFormat) {
        getData().put(name, outputFormat);
        writeData();
    }

    @Override
    public Map<String, OutputFormat> getEmptyData() {
        return new HashMap<>();
    }

    @Override
    protected TypeReference<Map<String, OutputFormat>> getTypeReference() {
        return new TypeReference<>() {};
    }
}
