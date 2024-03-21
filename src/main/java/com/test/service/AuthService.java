package com.test.service;

import static com.test.constant.TokenConstants.ACCESS_TOKEN_CLAIM;
import static com.test.constant.TokenConstants.ACCESS_TOKEN_SECRET;
import static com.test.constant.TokenConstants.ACCESS_TOKEN_TTL;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.test.dto.LoginRequest;
import com.test.dto.RegisterRequest;
import com.test.exception.EmailAlreadyRegisteredException;
import com.test.exception.InvalidCredentialsException;
import com.test.mapper.UserMapper;
import com.test.model.User;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

  private final UserMapper userMapper;
  private final UserService userService;

  @Inject
  public AuthService(UserMapper mapper, UserService service) {
    userMapper = mapper;
    userService = service;
  }

  public void register(RegisterRequest request) {
    if (userService.existsByEmail(request.email())) {
      throw new EmailAlreadyRegisteredException();
    }
    String passwordHash = BCrypt.hashpw(request.password(), BCrypt.gensalt());
    User user = userMapper.asEntity(request, passwordHash);
    userService.createUser(user);
  }

  public String login(LoginRequest request) {
    User user = userService.findEntityByEmail(request.email());
    if (!BCrypt.checkpw(request.password(), user.password())) {
      throw new InvalidCredentialsException();
    }
    return createAccessToken(user.id());
  }

  private String createAccessToken(UUID userId) {
    Date expirationDate = Date.from(Instant.now().plusSeconds(ACCESS_TOKEN_TTL));
    return JWT.create()
        .withClaim(ACCESS_TOKEN_CLAIM, userId.toString())
        .withExpiresAt(expirationDate)
        .sign(Algorithm.HMAC256(ACCESS_TOKEN_SECRET));
  }
}
