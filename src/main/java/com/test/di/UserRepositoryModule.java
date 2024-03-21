package com.test.di;

import com.test.repository.UserRepository;
import dagger.Module;
import dagger.Provides;

@Module
public class UserRepositoryModule {
  @Provides
  public static UserRepository provideUserRepository() {
    return new UserRepository();
  }
}
