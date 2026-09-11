package es.upm.miw.apaw.services.exceptions;

public class ClientBusinessException extends RuntimeException {
    public ClientBusinessException(String userMessage) {
        super(userMessage);
    }
}



