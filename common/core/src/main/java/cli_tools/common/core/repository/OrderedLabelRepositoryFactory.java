package cli_tools.common.core.repository;

public interface OrderedLabelRepositoryFactory {

    OrderedLabelRepository getOrderedLabelRepository(String orderedLabelType);

}
