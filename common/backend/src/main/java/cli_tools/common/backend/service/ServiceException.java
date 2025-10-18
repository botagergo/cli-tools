package cli_tools.common.backend.service;

public class ServiceException extends Exception {

    public ServiceException(String msg, Exception e) {
        super(msg, e);
    }

    public ServiceException(String msg) {
        super(msg);
    }

}
