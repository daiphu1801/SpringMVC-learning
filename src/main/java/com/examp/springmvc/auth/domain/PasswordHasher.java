package com.examp.springmvc.auth.domain;

public interface PasswordHasher {

    String hash(String rawPassword);

    boolean check(String rawPassword, String hashedPassword);
}
