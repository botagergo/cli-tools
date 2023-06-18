package task_manager.repository.view;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import task_manager.data.FilterCriterionInfo;
import task_manager.data.SortingCriterion;
import task_manager.data.SortingInfo;
import task_manager.data.ViewInfo;
import task_manager.repository.JsonRepository;
import task_manager.repository.ViewInfoRepository;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonViewInfoRepository extends JsonRepository<HashMap<String, ViewInfo>> implements ViewInfoRepository {

    @Inject
    public JsonViewInfoRepository(@Named("viewInfoJsonFile") File jsonFile) {
        super(jsonFile);
        getObjectMapper().addMixIn(ViewInfo.class, ViewInfoMixIn.class);
        getObjectMapper().addMixIn(SortingInfo.class, SortingInfoMixIn.class);
        getObjectMapper().addMixIn(SortingCriterion.class, SortingCriterionMixIn.class);
        getObjectMapper().addMixIn(FilterCriterionInfo.class, FilterCriterionInfoMixIn.class);
    }

    @Override
    public ViewInfo get(String name) throws IOException {
          return getData().getOrDefault(name, null);
    }

    @Override
    public HashMap<String, ViewInfo> getAll() throws IOException {
        return getData();
    }

    @Override
    public ViewInfo create(ViewInfo viewInfo) throws IOException {
        getData().put(viewInfo.name(), viewInfo);
        writeData();
        return viewInfo;
    }

    @Override
    public ViewInfo update(ViewInfo viewInfo) throws IOException {
        HashMap<String, ViewInfo> viewInfos = getData();
        if (!viewInfos.containsKey(viewInfo.name())) {
            return null;
        } else {
            return viewInfos.put(viewInfo.name(), new ViewInfo(viewInfo.name(), viewInfo.sortingInfo(), viewInfo.filterCriterionInfo()));
        }
    }

    @Override
    public boolean delete(String name) throws IOException {
        return getData().remove(name) != null;
    }

    @Override
    public HashMap<String, ViewInfo> getEmptyData() {
        return new HashMap<>();
    }

    @Override
    protected JavaType constructType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, ViewInfo.class);
    }
}
