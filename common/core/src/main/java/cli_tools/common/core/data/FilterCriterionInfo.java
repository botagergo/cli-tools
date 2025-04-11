package cli_tools.common.core.data;

import java.util.List;

public record FilterCriterionInfo(
        String name,
        Type type,
        String propertyName,
        List<FilterCriterionInfo> children,
        Predicate predicate,
        Predicate predicateNegated,
        List<Object> operands
) {

    public static Builder builder() {
        return new Builder();
    }

    public enum Type {
        PROPERTY,
        AND,
        OR,
        NOT
    }

    public static class Builder {
        private String name;
        private Type type;
        private String propertyName;
        private List<FilterCriterionInfo> children;
        private Predicate predicate;
        private Predicate predicateNegated;
        private List<Object> operands;

        public FilterCriterionInfo build() {
            return new FilterCriterionInfo(
                    name, type, propertyName, children, predicate, predicateNegated, operands
            );
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder propertyName(String propertyName) {
            this.propertyName = propertyName;
            return this;
        }

        public Builder children(List<FilterCriterionInfo> children) {
            this.children = children;
            return this;
        }

        public Builder predicate(Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder predicateNegated(Predicate predicateNegated) {
            this.predicateNegated = predicateNegated;
            return this;
        }

        public Builder operands(List<Object> operands) {
            this.operands = operands;
            return this;
        }
    }

}
