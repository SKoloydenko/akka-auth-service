package com.test.service;

import com.test.dto.UserResponse;
import com.test.exception.UserNotFoundException;
import com.test.mapper.UserMapper;
import com.test.model.User;
import com.test.repository.UserRepository;
import java.util.UUID;
import javax.inject.Inject;

public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Inject
  public UserService(UserRepository repository, UserMapper mapper) {
    userRepository = repository;
    userMapper = mapper;
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public User findEntityByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
  }

  public void createUser(User user) {
    userRepository.save(user);
  }

  public UserResponse findById(UUID id) {
    User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    return userMapper.asResponse(user);
  }
}
