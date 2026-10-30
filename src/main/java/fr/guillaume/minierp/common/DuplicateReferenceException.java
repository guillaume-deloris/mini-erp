package fr.guillaume.minierp.common;

public class DuplicateReferenceException extends BusinessException{
    public DuplicateReferenceException(String reference) {
        super("the reference " + reference + " already exists");
    }
}
