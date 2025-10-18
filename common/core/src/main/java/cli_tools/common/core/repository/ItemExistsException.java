package cli_tools.common.core.repository;

import lombok.Getter;

@Getter
public class ItemExistsException extends Exception {

    private final String itemName;

    public ItemExistsException(String itemName, Exception e) {
        super("'%s' already exists".formatted(itemName), e);
        this.itemName = itemName;
    }

}
