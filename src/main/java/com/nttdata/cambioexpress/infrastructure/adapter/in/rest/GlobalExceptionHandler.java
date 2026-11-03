package com.nttdata.cambioexpress.infrastructure.adapter.in.rest;

import com.nttdata.cambioexpress.domain.exception.ResourceNotFound;
import com.nttdata.cambioexpress.domain.exception.BusinessRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFound.class)
  public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFound ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", ex.getMessage()));
  }


  @ExceptionHandler({BusinessRuleException.class, IllegalArgumentException.class})
  public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", ex.getMessage()));
  }
}