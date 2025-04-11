package cli_tools.common.ordered_label.service;

import cli_tools.common.core.data.OrderedLabel;
import cli_tools.common.core.repository.OrderedLabelRepositoryFactory;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class OrderedLabelServiceImpl implements OrderedLabelService {

    private final OrderedLabelRepositoryFactory orderedLabelRepositoryFactory;

    @Override
    public OrderedLabel getOrderedLabel(String labelType, int labelValue) throws IOException {
        return orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).get(labelValue);
    }

    @Override
    public OrderedLabel findOrderedLabel(String labelType, String labelText) throws IOException {
        return orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).find(labelText);
    }

    @Override
    public void createOrderedLabel(String labelType, String labelText) throws IOException {
        orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).create(labelText);
    }

    @Override
    public List<OrderedLabel> getOrderedLabels(String labelType) throws IOException {
        return orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).getAll();
    }

    @Override
    public void deleteAllOrderedLabels(String labelType) throws IOException {
        orderedLabelRepositoryFactory.getOrderedLabelRepository(labelType).deleteAll();
    }

}
