package task_manager.data.filter;

public class FilterCriterion {

    public FilterCriterion(String propertyName, PredicateKind predicateKind, Object operand) {
        this.propertyName = propertyName;
        this.predicateKind = predicateKind;
        this.operand = operand;
    }

    public final String propertyName;
    public final PredicateKind predicateKind;
    public final Object operand;

}
