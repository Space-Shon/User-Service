package ru.headsandhands.userservice.Service.Impl;

import com.nimbusds.jose.JOSEException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.headsandhands.userservice.Model.Role;
import ru.headsandhands.userservice.Model.User;
import ru.headsandhands.userservice.Model.jwt.JwtTokenPair;
import ru.headsandhands.userservice.Repository.RepositoryUser;
import ru.headsandhands.userservice.Request.AuthenticationRequest;
import ru.headsandhands.userservice.Request.RequestRegister;
import ru.headsandhands.userservice.Response.AuthenticationResponse;
import ru.headsandhands.userservice.Service.security.ServiceJWTImpl;

@Service
public class AuthenticationService {

    private final RepositoryUser repositoryUser;
    private final PasswordEncoder passwordEncoder;
    private final ServiceJWTImpl serviceJWT;


    public AuthenticationService(RepositoryUser repositoryUser, PasswordEncoder passwordEncoder, ServiceJWTImpl serviceJWT) {
        this.repositoryUser = repositoryUser;
        this.passwordEncoder = passwordEncoder;
        this.serviceJWT = serviceJWT;
    }

    public AuthenticationResponse register(RequestRegister request) throws JOSEException {
        var user = User.builder()
                .login(request.login())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        repositoryUser.save(user);

        JwtTokenPair tokens = serviceJWT.buildJwtTokenPair(user, user.getRole());

        return new AuthenticationResponse(tokens.accessToken(), tokens.refreshToken());
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) throws JOSEException {
        var user = repositoryUser.findByLogin(request.getUserName())
                .orElseThrow();

        JwtTokenPair pair = serviceJWT.buildJwtTokenPair(user, user.getRole());

        return new AuthenticationResponse(pair.accessToken(), pair.refreshToken());
    }

    public void deleteUser(Integer id) {
        repositoryUser.deleteById(Long.valueOf(id));
    }
}