package com.savaava.mytvskeeper.exceptions;

public class VideoAlreadyExistsException extends Exception {
    public VideoAlreadyExistsException() {
        super();
    }
    public VideoAlreadyExistsException(String msg){
      super(msg);
    }
}
