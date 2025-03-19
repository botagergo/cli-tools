package cli_tools.common.core.repository;

public interface LabelRepositoryFactory {

    LabelRepository getLabelRepository(String labelType);

}
