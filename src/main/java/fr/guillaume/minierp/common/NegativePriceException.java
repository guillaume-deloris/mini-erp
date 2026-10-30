package fr.guillaume.minierp.common;

public class NegativePriceException extends BusinessException {
    public NegativePriceException(String message) {
        super(message);
    }
}
