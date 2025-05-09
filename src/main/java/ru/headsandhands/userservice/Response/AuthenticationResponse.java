package ru.headsandhands.userservice.Response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record AuthenticationResponse(
        String accessToken,
        String refreshToken

) {
}
