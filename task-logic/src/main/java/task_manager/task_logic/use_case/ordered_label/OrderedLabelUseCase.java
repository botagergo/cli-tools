package task_manager.task_logic.use_case.ordered_label;

import task_manager.core.data.OrderedLabel;

import java.io.IOException;

public interface OrderedLabelUseCase {
    OrderedLabel getOrderedLabel(String labelName, int labelValue) throws IOException;

    OrderedLabel findOrderedLabel(String labelType, String labelText) throws IOException;

    void createOrderedLabel(String labelName, String labelText) throws IOException;
}
