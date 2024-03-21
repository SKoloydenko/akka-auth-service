package com.test.di;

import com.test.mapper.UserMapper;
import dagger.Module;
import dagger.Provides;

@Module
public class UserMapperModule {
  @Provides
  public static UserMapper provideUserMapper() {
    return new UserMapper();
  }
}
