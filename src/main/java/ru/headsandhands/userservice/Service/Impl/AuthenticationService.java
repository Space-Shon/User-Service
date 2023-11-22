package ru.headsandhands.userservice.Service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.headsandhands.userservice.Model.Role;
import ru.headsandhands.userservice.Model.User;
import ru.headsandhands.userservice.Repository.RepositoryUser;
import ru.headsandhands.userservice.Request.AuthenticationRequest;
import ru.headsandhands.userservice.Request.RequestRegister;
import ru.headsandhands.userservice.Response.AuthenticationResponse;

import java.io.IOException;

@Service
public class AuthenticationService {

    private final RepositoryUser repositoryUser;
    private final PasswordEncoder passwordEncoder;
    private final ServiceJWTImpl serviceJWT;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(RepositoryUser repositoryUser, PasswordEncoder passwordEncoder, ServiceJWTImpl serviceJWT, AuthenticationManager authenticationManager) {
        this.repositoryUser = repositoryUser;
        this.passwordEncoder = passwordEncoder;
        this.serviceJWT = serviceJWT;
        this.authenticationManager = authenticationManager;
    }


    public AuthenticationResponse register(RequestRegister request) {
        var user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repositoryUser.save(user);
        var JwtToken = serviceJWT.generateToken(user);
        var refreshToken = serviceJWT.generateRefreshToken(user);
        return AuthenticationResponse
                .builder()
                .accessToken(JwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );
        var user = repositoryUser.findByUsername(request.getUserName())
                .orElseThrow();
        var JwtToken = serviceJWT.generateToken(user);
        var refreshToken = serviceJWT.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(JwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userName;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userName = serviceJWT.extractUserName(refreshToken);
        if (userName != null) {
            var userDetails = this.repositoryUser.findByUsername(userName).orElseThrow();
            if (serviceJWT.isTokenValid(refreshToken, userDetails)) {
                var accessToken = serviceJWT.generateToken(userDetails);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}