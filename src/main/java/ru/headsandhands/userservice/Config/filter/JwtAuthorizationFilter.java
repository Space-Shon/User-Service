package ru.headsandhands.userservice.Config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.headsandhands.userservice.Model.jwt.JwtToken;
import ru.headsandhands.userservice.Service.JWTService;
import ru.headsandhands.userservice.Service.security.JwtAuthServiceImpl;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final JwtAuthServiceImpl jwtAuthService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (trySetAuthorization(request, response)) {
            filterChain.doFilter(request, response);
        }
    }

    private Boolean trySetAuthorization(HttpServletRequest request, HttpServletResponse response) {
        try {
            setAuthorization(request);
            return true;
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            return false;
        }
    }

    private void setAuthorization(HttpServletRequest request) {
        Optional<String> authorizationHeader = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (authorizationHeader.isPresent()) {
            JwtToken token = jwtService.parseJwtToken(authorizationHeader.get());
            if (!token.isExpired() && token.isAccessToken())
                jwtAuthService.setAuthentication(token);
        }
    }
}
