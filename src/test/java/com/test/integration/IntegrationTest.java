package com.test.integration;

import static com.test.constant.H2Constants.DB_CONNECTION_URL;

import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.headers.Cookie;
import akka.http.javadsl.model.headers.HttpCookie;
import akka.http.javadsl.model.headers.SetCookie;
import akka.http.javadsl.testkit.JUnitRouteTest;
import akka.http.javadsl.testkit.TestRoute;
import com.test.di.AppComponent;
import com.test.di.DaggerAppComponent;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTest extends JUnitRouteTest {

  private final AppComponent appComponent = DaggerAppComponent.create();

  @Before
  public void setupDB() {
    try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
        Statement statement = connection.createStatement()) {
      statement.execute(
          "CREATE TABLE IF NOT EXISTS users (id UUID PRIMARY KEY, created TIMESTAMP, email VARCHAR UNIQUE, password VARCHAR, name VARCHAR)");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @After
  public void clearDB() {
    try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
        Statement statement = connection.createStatement()) {
      statement.execute("TRUNCATE TABLE users");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void loginAfterRegistration() {
    String testCase = "login-after-registration";

    TestRoute authRoute = testRoute(appComponent.authController().routes());

    String registrationRequest = readResource(testCase + "/registrationRequest.json");
    String loginRequest = readResource(testCase + "/loginRequest.json");

    authRoute
        .run(
            HttpRequest.POST("/api_v1/registrate")
                .withEntity(ContentTypes.APPLICATION_JSON, registrationRequest))
        .assertStatusCode(StatusCodes.OK);

    authRoute
        .run(
            HttpRequest.POST("/api_v1/login")
                .withEntity(ContentTypes.APPLICATION_JSON, loginRequest))
        .assertStatusCode(StatusCodes.OK);
  }

  @Test
  public void loginWithUnusedEmailFailure() {
    String testCase = "login-with-unused-email-failure";

    TestRoute authRoute = testRoute(appComponent.authController().routes());

    String loginRequest = readResource(testCase + "/loginRequest.json");

    authRoute
        .run(
            HttpRequest.POST("/api_v1/login")
                .withEntity(ContentTypes.APPLICATION_JSON, loginRequest))
        .assertStatusCode(StatusCodes.UNPROCESSABLE_CONTENT);
  }

  @Test
  public void getUserDataAfterLogin() {
    String testCase = "get-user-data-after-login";

    TestRoute authRoute = testRoute(appComponent.authController().routes());
    TestRoute userRoute = testRoute(appComponent.userController().routes());

    String registrationRequest = readResource(testCase + "/registrationRequest.json");
    String loginRequest = readResource(testCase + "/loginRequest.json");

    authRoute
        .run(
            HttpRequest.POST("/api_v1/registrate")
                .withEntity(ContentTypes.APPLICATION_JSON, registrationRequest))
        .assertStatusCode(StatusCodes.OK);

    HttpCookie cookie =
        authRoute
            .run(
                HttpRequest.POST("/api_v1/login")
                    .withEntity(ContentTypes.APPLICATION_JSON, loginRequest))
            .assertStatusCode(StatusCodes.OK)
            .header(SetCookie.class)
            .cookie();

    userRoute
        .run(HttpRequest.GET("/api_v1/me").addHeader(Cookie.create(cookie.pair())))
        .assertStatusCode(StatusCodes.OK);
  }

  @Test
  public void getUserDataWithNoAuthFailure() {
    TestRoute userRoute = testRoute(appComponent.userController().routes());

    userRoute.run(HttpRequest.GET("/api_v1/me")).assertStatusCode(StatusCodes.UNAUTHORIZED);
  }

  private String readResource(String path) {
    try (InputStream is = getClass().getClassLoader().getResource(path).openStream()) {
      byte[] bytes = is.readAllBytes();
      return new String(bytes);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }
}
