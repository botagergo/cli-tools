package cli_tools.common.core.data;

import java.util.List;

public record ViewInfo(
        SortingInfo sortingInfo,
        FilterCriterionInfo filterCriterionInfo,
        List<String> propertiesToList,
        OutputFormat outputFormat,
        boolean hierarchical,
        boolean listDone
) {
    public static class Builder {
        private SortingInfo sortingInfo;
        private FilterCriterionInfo filterCriterionInfo;
        private List<String> propertiesToList;
        private OutputFormat outputFormat;
        private boolean hierarchical = false;
        private boolean listDone = false;

        public ViewInfo build() {
            return new ViewInfo(
                    sortingInfo, filterCriterionInfo, propertiesToList, outputFormat, hierarchical, listDone
            );
        }

        public Builder sortingInfo(SortingInfo sortingInfo) {
            this.sortingInfo = sortingInfo;
            return this;
        }

        public Builder filterCriterionInfo(FilterCriterionInfo filterCriterionInfo) {
            this.filterCriterionInfo = filterCriterionInfo;
            return this;
        }

        public Builder propertiesToList(List<String> propertiesToList) {
            this.propertiesToList = propertiesToList;
            return this;
        }

        public Builder outputFormat(OutputFormat outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        public Builder hierarchical(boolean hierarchical) {
            this.hierarchical = hierarchical;
            return this;
        }

        public Builder listDone(boolean listDone) {
            this.listDone = listDone;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
