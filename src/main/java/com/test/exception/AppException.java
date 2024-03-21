package com.test.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
  "localizedMessage",
  "cause",
  "stackTrace",
  "ourStackTrace",
  "suppressed",
  "message"
})
public class AppException extends RuntimeException {}
