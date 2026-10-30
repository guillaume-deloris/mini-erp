package fr.guillaume.minierp.common;

public abstract class BusinessException extends RuntimeException{
    protected BusinessException (String message) {
        super(message);
    }
}
