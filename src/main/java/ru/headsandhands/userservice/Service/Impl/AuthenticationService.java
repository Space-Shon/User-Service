package ru.headsandhands.userservice.Service.Impl;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.headsandhands.userservice.Model.Role;
import ru.headsandhands.userservice.Model.Token;
import ru.headsandhands.userservice.Model.TokenType;
import ru.headsandhands.userservice.Model.User;
import ru.headsandhands.userservice.Repository.RepositoryToken;
import ru.headsandhands.userservice.Repository.RepositoryUser;
import ru.headsandhands.userservice.Request.AuthenticationRequest;
import ru.headsandhands.userservice.Request.RequestRegister;
import ru.headsandhands.userservice.Response.AuthenticationResponse;
import ru.headsandhands.userservice.Thread.ThreadLocalPayload;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

@Service
public class AuthenticationService {

    private final RepositoryUser repositoryUser;
    private final PasswordEncoder passwordEncoder;
    private final ServiceJWTImpl serviceJWT;

    private final RepositoryToken repositoryToken;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(RepositoryUser repositoryUser, PasswordEncoder passwordEncoder, ServiceJWTImpl serviceJWT, RepositoryToken repositoryToken, AuthenticationManager authenticationManager) {
        this.repositoryUser = repositoryUser;
        this.passwordEncoder = passwordEncoder;
        this.serviceJWT = serviceJWT;
        this.repositoryToken = repositoryToken;
        this.authenticationManager = authenticationManager;
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .typeToken(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        repositoryToken.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = repositoryToken.findAllValidTokensByUser(Math.toIntExact(user.getId()));
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        repositoryToken.saveAll(validUserTokens);
    }

    public AuthenticationResponse register(RequestRegister request) {
        var user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repositoryUser.save(user);
        var savedUser = repositoryUser.save(user);
        var JwtToken = serviceJWT.generateToken(user);
        var refreshToken = serviceJWT.generateRefreshToken(user);
        saveUserToken(savedUser, JwtToken);
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
        revokeAllUserTokens(user);
        saveUserToken(user, JwtToken);
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
                revokeAllUserTokens(userDetails);
                saveUserToken(userDetails, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void deleteUser(Integer id) {
        Token token = repositoryToken.findById(Long.valueOf(id)).orElse(null);
        if (token != null) {
            repositoryToken.delete(token);
        }
        repositoryUser.deleteById(Long.valueOf(id));
    }
}