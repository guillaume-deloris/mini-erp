package fr.guillaume.minierp.common;

/**
 * JSON body of every error: {"error": "..."}
 */
public record ErrorResponse(String error) {
}