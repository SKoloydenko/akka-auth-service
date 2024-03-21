package com.test.mapper;

import com.test.dto.RegisterRequest;
import com.test.dto.UserResponse;
import com.test.model.User;

public class UserMapper {
  public User asEntity(RegisterRequest request, String passwordHash) {
    return new User(request.email(), passwordHash, request.name());
  }

  public UserResponse asResponse(User user) {
    return new UserResponse(user.id(), user.email(), user.created(), user.name());
  }
}
