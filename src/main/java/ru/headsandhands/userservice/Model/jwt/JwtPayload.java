package ru.headsandhands.userservice.Model.jwt;

import ru.headsandhands.userservice.Model.Role;

import java.time.LocalDateTime;

public record JwtPayload(
    Long id,
    Role role,
    LocalDateTime expiresAt
) {
}
