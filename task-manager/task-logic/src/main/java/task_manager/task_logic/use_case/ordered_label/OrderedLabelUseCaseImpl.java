package task_manager.task_logic.use_case.ordered_label;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.core.data.OrderedLabel;
import task_manager.core.repository.OrderedLabelRepositoryFactory;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class OrderedLabelUseCaseImpl implements OrderedLabelUseCase {

    @Override
    public OrderedLabel getOrderedLabel(String labelType, int labelValue) throws IOException {
        return orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).get(labelValue);
    }

    private final OrderedLabelRepositoryFactory orderedLabelRepositoryFactory;

    @Override
    public void createOrderedLabel(String labelType, String labelText) throws IOException {
        orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).create(labelText);
    }

    @Override
    public OrderedLabel findOrderedLabel(String labelType, String labelText) throws IOException {
        return orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).find(labelText);
    }

}
