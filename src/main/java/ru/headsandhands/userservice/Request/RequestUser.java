package ru.headsandhands.userservice.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestUser {

    @NotBlank
    private String name;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;


}
