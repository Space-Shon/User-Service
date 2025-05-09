package ru.headsandhands.userservice.Service;

import com.nimbusds.jose.JOSEException;
import ru.headsandhands.userservice.Model.Role;
import ru.headsandhands.userservice.Model.jwt.JwtToken;
import ru.headsandhands.userservice.Model.jwt.JwtTokenPair;
import ru.headsandhands.userservice.Model.User;

public interface JWTService {

    public JwtTokenPair buildJwtTokenPair(User user, Role role) throws JOSEException;

    public JwtToken parseJwtToken(String token);
}
