package fr.guillaume.minierp.common;

public class MissingNameNegativeStockException extends BusinessException{
    public MissingNameNegativeStockException(String message) {
        super(message);
    }
}
