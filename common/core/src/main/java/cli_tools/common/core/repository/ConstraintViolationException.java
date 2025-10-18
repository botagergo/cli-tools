package cli_tools.common.core.repository;

public class ConstraintViolationException extends DataAccessException {

    public ConstraintViolationException(String msg, Exception e) {
        super(msg, e);
    }

    public ConstraintViolationException(String msg) {
        super(msg);
    }

}
