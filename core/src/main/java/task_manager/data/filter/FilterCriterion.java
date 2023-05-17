package task_manager.data.filter;

@SuppressWarnings("unused")
public record FilterCriterion(String propertyName, PredicateKind predicateKind, Object operand) {

}
