package ru.headsandhands.userservice.Model.jwt;

public record JwtTokenPair(
        String accessToken,
        String refreshToken
) {
}
