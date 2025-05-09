package ru.headsandhands.userservice.Model.jwt;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class JwtToken {

    private final JwtPayload payload;

    public JwtToken(JwtPayload payload) {
        this.payload = payload;
    }

    public static class AccessToken extends JwtToken {
        public AccessToken(JwtPayload payload) {
            super(payload);
        }
    }

    public static class RefreshToken extends JwtToken {
        public RefreshToken(JwtPayload payload) {
            super(payload);
        }
    }

    public final Boolean isAccessToken() {
        return this instanceof AccessToken;
    }

    public final Boolean isRefreshToken() {
        return this instanceof RefreshToken;
    }

    public final Boolean isExpired() {
        return this.payload.expiresAt().isBefore(LocalDateTime.now());
    }
}
