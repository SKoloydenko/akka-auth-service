package com.test.di;

import com.test.mapper.UserMapper;
import com.test.service.AuthService;
import com.test.service.UserService;
import dagger.Module;
import dagger.Provides;

@Module
public class AuthServiceModule {
  @Provides
  public static AuthService provideAuthService(UserService service, UserMapper mapper) {
    return new AuthService(mapper, service);
  }
}
