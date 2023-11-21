package ru.headsandhands.userservice.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.headsandhands.userservice.Service.Impl.ServiceJWTImpl;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final ServiceJWTImpl serviceJWT;
    private final UserDetailsService userDetailsService;
    public JWTAuthenticationFilter(ServiceJWTImpl serviceJWT, UserDetailsService userDetailsService){
        this.serviceJWT = serviceJWT;
        this.userDetailsService = userDetailsService;
    }




    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String JWT;
        final String userName;
        if(authHeader == null  || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        JWT = authHeader.substring(7);
        userName = serviceJWT.extractUserName(JWT);
        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUserName(userName);
        }
    }

}
