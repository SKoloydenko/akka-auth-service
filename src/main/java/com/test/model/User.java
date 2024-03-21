package com.test.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(UUID id, LocalDateTime created, String email, String password, String name) {
  public User(String email, String password, String name) {
    this(UUID.randomUUID(), LocalDateTime.now(), email, password, name);
  }
}
