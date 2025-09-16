package cli_tools.common.backend.ordered_label.service;

import cli_tools.common.core.data.OrderedLabel;

import java.io.IOException;
import java.util.List;

public interface OrderedLabelService {
    OrderedLabel getOrderedLabel(String labelType, int labelValue) throws IOException;

    OrderedLabel findOrderedLabel(String labelType, String labelText) throws IOException;

    void createOrderedLabel(String labelType, String labelText) throws IOException;

    List<OrderedLabel> getOrderedLabels(String labelType) throws IOException;

    void deleteAllOrderedLabels(String labelType) throws IOException;
}
