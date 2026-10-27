package fr.guillaume.minierp.hello;

import java.time.LocalDateTime;

/**
 * Simple object returned as JSON.
 * JSON-B uses the public getters to build the JSON: {"message": "...", "date": "..."}
 * Carries the response and datas
 */
public class HelloMessage {

    private String message;
    private LocalDateTime date;

    private int length;

    // JSON-B needs a no-argument constructor to deserialize
    public HelloMessage() {
    }

    public HelloMessage(String message) {
        this.message = message;
        this.length = message.length();
        this.date = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
