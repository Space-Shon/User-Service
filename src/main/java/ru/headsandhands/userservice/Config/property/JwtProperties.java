package ru.headsandhands.userservice.Config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        Long accessTokenTtl,
        Long refreshTokenTtl,
        String publicKey,
        String privateKey
) {
}
