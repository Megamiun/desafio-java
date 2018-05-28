package br.com.gabryel.logineer.service.impl;

import br.com.gabryel.logineer.service.TokenManager;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenManagerImpl implements TokenManager {

    private final String SECRET = "MySecret";

    @Override
    public String createToken(String uuid, String email, LocalDateTime lastLogin) {
        Date lastLoginDate = toDate(lastLogin);

        return JWT.create()
            .withIssuedAt(lastLoginDate)
            .withExpiresAt(toDate(lastLogin.plusMinutes(30)))
            .withClaim("uuid", uuid)
            .withIssuer("logineer")
            .withSubject(email)
            .sign(getAlgorithm());
    }

    private Algorithm getAlgorithm() {
        try {
            return Algorithm.HMAC256(SECRET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.toInstant(ZoneOffset.ofHours(0)));
    }
}
