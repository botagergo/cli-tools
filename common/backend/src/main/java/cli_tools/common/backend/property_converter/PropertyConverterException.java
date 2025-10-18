package cli_tools.common.backend.property_converter;

import cli_tools.common.backend.service.ServiceException;
import lombok.Getter;

@Getter
public class PropertyConverterException extends ServiceException {

    public PropertyConverterException(String msg, Exception e) {
        super(msg, e);
    }

}
