package fr.guillaume.minierp.common;

/**
 * An external system (LN, ION...) is unreachable or answered with an error.
 * Technical, not business: it is not the client's fault.
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}