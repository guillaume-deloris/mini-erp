package fr.guillaume.minierp.common;

public class InvalidStatusException extends BusinessException {
    public InvalidStatusException(String message) {
        super(message);
    }
}
