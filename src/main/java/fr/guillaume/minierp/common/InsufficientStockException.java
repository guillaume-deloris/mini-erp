package fr.guillaume.minierp.common;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException(String reference, int available, int requested) {
        super("Insufficient stock for product " + reference + ": "
                + available + " available, " + requested + " requested");
    }
}
