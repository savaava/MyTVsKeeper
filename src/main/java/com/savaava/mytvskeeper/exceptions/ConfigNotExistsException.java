package com.savaava.mytvskeeper.exceptions;

public class ConfigNotExistsException extends Exception {
  public ConfigNotExistsException() {
    super();
  }
  public ConfigNotExistsException(String msg){
    super(msg);
  }
}
