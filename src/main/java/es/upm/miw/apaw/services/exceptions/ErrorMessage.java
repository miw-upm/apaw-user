package es.upm.miw.apaw.services.exceptions;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorMessage {

    private final String error;
    private final String message;
    private final String cause;

    public ErrorMessage(Exception exception) {
        this.error = exception.getClass().getSimpleName();
        if (exception instanceof ApiException apiException) {
            this.message = apiException.getDetail();
            this.cause = apiException.getCauseDetail();
        } else {
            this.message = exception.getMessage();
            this.cause = "";
        }
    }
}
