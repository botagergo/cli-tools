package cli_tools.task_manager.backend.task.repository;

import cli_tools.common.backend.repository.SimpleJsonRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.File;
import java.util.ArrayList;

public class SimpleJsonRepositoryImpl extends SimpleJsonRepository<ArrayList<Integer>> {

    public SimpleJsonRepositoryImpl(File jsonFile) {
        super(jsonFile);
    }

    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, Integer.class);
    }

    public ArrayList<Integer> getEmptyData() {
        return new ArrayList<>();
    }

}
