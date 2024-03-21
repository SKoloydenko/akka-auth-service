package com.test;

import static akka.http.javadsl.server.Directives.concat;
import static com.test.constant.H2Constants.DB_CONNECTION_URL;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import com.test.controller.AuthController;
import com.test.controller.UserController;
import com.test.di.AppComponent;
import com.test.di.DaggerAppComponent;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletionStage;

public class App {

  private static final AppComponent appComponent = DaggerAppComponent.create();

  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create();
    final Http http = Http.get(system);

    App app = new App();
    app.setupDB();

    final CompletionStage<ServerBinding> futureBinding =
        http.newServerAt("localhost", 8080).bind(app.routes());

    futureBinding.whenComplete(
        (binding, exception) -> {
          if (binding != null) {
            InetSocketAddress address = binding.localAddress();
            system
                .log()
                .info(
                    "Server listening at http://{}:{}/",
                    address.getHostString(),
                    address.getPort());
          } else {
            system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
            system.terminate();
          }
        });
  }

  private Route routes() {
    AuthController authController = appComponent.authController();
    UserController userController = appComponent.userController();
    return concat(authController.routes(), userController.routes());
  }

  private void setupDB() {
    try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
        Statement statement = connection.createStatement()) {
      statement.execute(
          "CREATE TABLE users (id UUID PRIMARY KEY, created TIMESTAMP, email VARCHAR UNIQUE, password VARCHAR, name VARCHAR)");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
