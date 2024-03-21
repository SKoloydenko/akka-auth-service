package com.test.controller;

import static com.test.constant.TokenConstants.ACCESS_TOKEN_COOKIE_NAME;
import static com.test.constant.TokenConstants.ACCESS_TOKEN_TTL;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.headers.HttpCookie;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.Route;
import com.test.dto.LoginRequest;
import com.test.dto.RegisterRequest;
import com.test.exception.AppExceptionHandler;
import com.test.service.AuthService;
import javax.inject.Inject;

public class AuthController extends AllDirectives {

  private final AuthService authService;
  private final ExceptionHandler exceptionHandler = AppExceptionHandler.get();

  @Inject
  public AuthController(AuthService service) {
    authService = service;
  }

  public Route routes() {
    return handleExceptions(
        exceptionHandler,
        () -> pathPrefix("api_v1", () -> concat(registerRoute(), loginRoute(), logoutRoute())));
  }

  private Route registerRoute() {
    return path(
        "registrate",
        () ->
            post(
                () ->
                    entity(
                        Jackson.unmarshaller(RegisterRequest.class),
                        request -> {
                          authService.register(request);
                          return complete(StatusCodes.OK, "");
                        })));
  }

  private Route loginRoute() {
    return path(
        "login",
        () ->
            post(
                () ->
                    entity(
                        Jackson.unmarshaller(LoginRequest.class),
                        request -> {
                          String token = authService.login(request);
                          return setCookie(
                              generateAccessTokenCookie(token), () -> complete(StatusCodes.OK, ""));
                        })));
  }

  private Route logoutRoute() {
    return path(
        "logout",
        () -> put(() -> setCookie(resetAccessTokenCookie(), () -> complete(StatusCodes.OK, ""))));
  }

  private HttpCookie generateAccessTokenCookie(String token) {
    return HttpCookie.create(ACCESS_TOKEN_COOKIE_NAME, token).withMaxAge(ACCESS_TOKEN_TTL);
  }

  private HttpCookie resetAccessTokenCookie() {
    return HttpCookie.create(ACCESS_TOKEN_COOKIE_NAME, "").withMaxAge(0);
  }
}
