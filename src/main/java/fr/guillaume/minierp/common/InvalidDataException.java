package fr.guillaume.minierp.common;

public class InvalidDataException extends BusinessException {

    public InvalidDataException(String message) {
        super(message);
    }
}