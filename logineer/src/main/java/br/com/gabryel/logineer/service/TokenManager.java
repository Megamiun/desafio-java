package br.com.gabryel.logineer.service;

import java.time.LocalDateTime;

public interface TokenManager {
    String createToken(String uuid, String email, LocalDateTime lastLogin);
}
