package es.upm.miw.apaw.services.exceptions;

public class BadRequestException extends ApiException {
    private static final String DESCRIPTION = "Bad Request Exception";

    public BadRequestException(String detail) {
        super(DESCRIPTION, detail);
    }

    public BadRequestException(String detail, Throwable cause) {
        super(DESCRIPTION, detail, cause);
    }
}
