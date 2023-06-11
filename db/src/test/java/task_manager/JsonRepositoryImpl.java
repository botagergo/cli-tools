package task_manager;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import task_manager.repository.JsonRepository;

import java.io.File;
import java.util.ArrayList;

public class JsonRepositoryImpl extends JsonRepository<ArrayList<Integer>> {

    public JsonRepositoryImpl(File jsonFile) {
        super(jsonFile);
    }

    public ArrayList<Integer> getEmptyData() {
        return new ArrayList<>();
    }

    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructCollectionType(ArrayList.class, Integer.class);
    }

}
