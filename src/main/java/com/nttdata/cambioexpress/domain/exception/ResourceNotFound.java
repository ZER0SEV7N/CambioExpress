package com.nttdata.cambioexpress.domain.exception;

public class ResourceNotFound extends RuntimeException {
  public ResourceNotFound(String message) {
    super(message);
  }
}
