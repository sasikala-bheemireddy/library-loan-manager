package com.example.library;

/** Thrown when a library operation breaks a rule (unknown book, book already on loan, and so on). */
public class LibraryException extends RuntimeException {
    public LibraryException(String message) {
        super(message);
    }
}
