package task_manager;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import task_manager.repository.SimpleJsonRepository;

import java.io.File;
import java.util.ArrayList;

public class SimpleJsonRepositoryImpl extends SimpleJsonRepository<ArrayList<Integer>> {

    public SimpleJsonRepositoryImpl(File jsonFile) {
        super(jsonFile);
    }

    public ArrayList<Integer> getEmptyData() {
        return new ArrayList<>();
    }

    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, Integer.class);
    }

}
