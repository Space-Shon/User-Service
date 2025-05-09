package ru.headsandhands.userservice.Service.security;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.headsandhands.userservice.Model.jwt.JwtPayload;
import ru.headsandhands.userservice.Model.jwt.JwtToken;

import java.util.List;
import java.util.Optional;

@Service
public class JwtAuthServiceImpl {

    private SecurityContext getSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    public void setAuthentication(JwtToken jwtToken) {
        this.getSecurityContext().setAuthentication(new UsernamePasswordAuthenticationToken(jwtToken, null, List.of()));
    }

    public Optional<JwtPayload> getPayloadOrNull() {
        return Optional.of(
                ((JwtToken) getSecurityContext().getAuthentication().getPrincipal()).getPayload()
        );
    }
}
