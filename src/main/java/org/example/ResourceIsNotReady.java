package org.example;

public class ResourceIsNotReady extends RuntimeException {
    public ResourceIsNotReady(String message) {
        super(message);
    }
}
