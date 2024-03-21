package com.test.repository;

import static com.test.constant.H2Constants.DB_CONNECTION_URL;

import com.test.model.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

  public boolean existsByEmail(String email) {
    return findByEmail(email).isPresent();
  }

  public Optional<User> findByEmail(String email) {
    try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
        PreparedStatement statement =
            connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
      statement.setString(1, email);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        User user = resultSetToUser(rs);
        return Optional.of(user);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  public Optional<User> findById(UUID id) {
    try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
        PreparedStatement statement =
            connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
      statement.setString(1, id.toString());
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        User user = resultSetToUser(rs);
        return Optional.of(user);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  public void save(User user) {
    try (Connection connection = DriverManager.getConnection(DB_CONNECTION_URL);
        PreparedStatement statement =
            connection.prepareStatement(
                "INSERT INTO users (id, created, email, password, name) VALUES (?, ?, ?, ?, ?)")) {
      statement.setString(1, user.id().toString());
      statement.setString(2, user.created().toString());
      statement.setString(3, user.email());
      statement.setString(4, user.password());
      statement.setString(5, user.name());
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private User resultSetToUser(ResultSet rs) throws SQLException {
    return new User(
        UUID.fromString(rs.getString("id")),
        rs.getTimestamp("created").toLocalDateTime(),
        rs.getString("email"),
        rs.getString("password"),
        rs.getString("name"));
  }
}
