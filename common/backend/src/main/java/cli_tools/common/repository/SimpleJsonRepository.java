package cli_tools.common.repository;

import java.io.File;

public class SimpleJsonRepository<T> extends JsonRepository<T, T> {

    public SimpleJsonRepository(File jsonFile) {
        super(jsonFile);
    }

    @Override
    protected T jsonToStoredData(T data) {
        return data;
    }

    @Override
    protected T storedToJsonData(T data) {
        return data;
    }

}
