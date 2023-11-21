package ru.headsandhands.userservice.Service;

import java.security.Key;

public interface ServiceJWT {

    public String extractUserName(String token);

}
