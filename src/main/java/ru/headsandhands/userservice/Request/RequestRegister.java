package ru.headsandhands.userservice.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record RequestRegister(

        @NotBlank
        String login,

        @NotBlank
        String password
) { }
