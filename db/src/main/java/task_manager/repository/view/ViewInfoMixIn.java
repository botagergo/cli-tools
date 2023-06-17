package task_manager.repository.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import task_manager.data.FilterCriterionInfo;

public record ViewInfoMixIn(String name,
         @JsonDeserialize(using = FilterCriterionInfoDeserializer.class)
         FilterCriterionInfo filterCriterionInfo
) {}
