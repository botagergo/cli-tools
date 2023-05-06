package task_manager.db.filter;

public class FilterCriterion {

    public FilterCriterion(String propertyName, PredicateKind predicateKind, Object operand) {
        this.propertyName = propertyName;
        this.predicateKind = predicateKind;
        this.operand = operand;
    }

    public String propertyName;
    public PredicateKind predicateKind;
    public Object operand;

}
