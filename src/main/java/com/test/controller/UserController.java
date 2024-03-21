package com.test.controller;

import static com.test.constant.TokenConstants.ACCESS_TOKEN_CLAIM;
import static com.test.constant.TokenConstants.ACCESS_TOKEN_COOKIE_NAME;
import static com.test.constant.TokenConstants.ACCESS_TOKEN_SECRET;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.Route;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.dto.UserResponse;
import com.test.exception.AppExceptionHandler;
import com.test.exception.UnauthorizedException;
import com.test.service.UserService;
import java.util.UUID;
import javax.inject.Inject;

public class UserController extends AllDirectives {

  private final UserService userService;
  private final ObjectMapper objectMapper;
  private final ExceptionHandler exceptionHandler = AppExceptionHandler.get();

  @Inject
  public UserController(UserService service, ObjectMapper mapper) {
    userService = service;
    objectMapper = mapper;
  }

  public Route routes() {
    return handleExceptions(
        exceptionHandler,
        () ->
            pathPrefix(
                "api_v1",
                () ->
                    path(
                        "me",
                        () ->
                            get(
                                () ->
                                    optionalCookie(
                                        ACCESS_TOKEN_COOKIE_NAME,
                                        cookieOptional -> {
                                          if (cookieOptional.isEmpty()) {
                                            throw new UnauthorizedException();
                                          }
                                          String token = cookieOptional.get().value();
                                          UserResponse response =
                                              userService.findById(getClaim(token));
                                          return complete(
                                              StatusCodes.OK,
                                              response,
                                              Jackson.marshaller(objectMapper));
                                        })))));
  }

  private UUID getClaim(String token) {
    try {
      JWTVerifier verifier = JWT.require(Algorithm.HMAC256(ACCESS_TOKEN_SECRET)).build();
      DecodedJWT jwt = verifier.verify(token);
      return UUID.fromString(jwt.getClaim(ACCESS_TOKEN_CLAIM).asString());
    } catch (JWTVerificationException e) {
      throw new UnauthorizedException();
    }
  }
}
