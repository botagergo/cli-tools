package cli_tools.common.view.repository;

import cli_tools.common.repository.SimpleJsonRepository;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import cli_tools.common.core.data.FilterCriterionInfo;
import cli_tools.common.core.data.SortingCriterion;
import cli_tools.common.core.data.SortingInfo;
import cli_tools.common.core.data.ViewInfo;
import cli_tools.common.core.repository.ViewInfoRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class JsonViewInfoRepository extends SimpleJsonRepository<HashMap<String, ViewInfo>> implements ViewInfoRepository {

    @Inject
    public JsonViewInfoRepository(@Named("viewJsonFile") File jsonFile) {
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
    public void create(String name, ViewInfo viewInfo) throws IOException {
        getData().put(name, viewInfo);
        writeData();
    }

    @Override
    public void deleteAll() throws IOException {
        getData().clear();
        writeData();
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
