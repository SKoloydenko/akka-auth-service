package com.test.di;

import com.test.controller.AuthController;
import com.test.controller.UserController;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
      AuthServiceModule.class,
      UserServiceModule.class,
      UserMapperModule.class,
      UserRepositoryModule.class,
      ObjectMapperModule.class
    })
public interface AppComponent {
  AuthController authController();

  UserController userController();
}
