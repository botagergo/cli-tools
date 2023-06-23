package task_manager.logic.use_case.ordered_label;

import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import task_manager.data.OrderedLabel;
import task_manager.repository.OrderedLabelRepositoryFactory;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class OrderedLabelUseCaseImpl implements OrderedLabelUseCase {

    private final OrderedLabelRepositoryFactory orderedLabelRepositoryFactory;

    @Override
    public OrderedLabel getOrderedLabel(String labelType, int labelValue) throws IOException {
        return orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).get(labelValue);
    }

    @Override
    public void createOrderedLabel(String labelType, String labelText) throws IOException {
        orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).create(labelText);
    }

}
