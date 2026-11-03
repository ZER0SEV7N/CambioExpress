package com.nttdata.cambioexpress.domain.exception;

public class BusinessRuleException extends RuntimeException {
  public BusinessRuleException(String message) {
    super(message);
  }
}
