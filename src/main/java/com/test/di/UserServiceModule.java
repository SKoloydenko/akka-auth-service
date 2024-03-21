package com.test.di;

import com.test.mapper.UserMapper;
import com.test.repository.UserRepository;
import com.test.service.UserService;
import dagger.Module;
import dagger.Provides;

@Module
public class UserServiceModule {
  @Provides
  public static UserService provideUserService(UserMapper mapper, UserRepository repository) {
    return new UserService(repository, mapper);
  }
}
