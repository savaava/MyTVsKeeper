package com.savaava.mytvskeeper.exceptions;

public class NotMatchingVideoTypeException extends Exception {
    public NotMatchingVideoTypeException() {
      super();
    }

    public NotMatchingVideoTypeException(String message) {
        super(message);
    }
}
