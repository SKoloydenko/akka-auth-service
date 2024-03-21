package com.test.exception;

import static akka.http.javadsl.server.Directives.complete;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.ExceptionHandler;

public class AppExceptionHandler {

  public static ExceptionHandler get() {
    return ExceptionHandler.newBuilder()
        .match(
            InvalidCredentialsException.class,
            e -> complete(StatusCodes.UNPROCESSABLE_CONTENT, e, Jackson.marshaller()))
        .match(
            EmailAlreadyRegisteredException.class,
            e -> complete(StatusCodes.UNPROCESSABLE_CONTENT, e, Jackson.marshaller()))
        .match(
            UserNotFoundException.class,
            e -> complete(StatusCodes.UNPROCESSABLE_CONTENT, e, Jackson.marshaller()))
        .match(
            UnauthorizedException.class,
            e -> complete(StatusCodes.UNAUTHORIZED, e, Jackson.marshaller()))
        .build();
  }
}
