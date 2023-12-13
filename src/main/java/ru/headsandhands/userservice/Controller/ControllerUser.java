package ru.headsandhands.userservice.Controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.headsandhands.userservice.Request.AuthenticationRequest;
import ru.headsandhands.userservice.Request.RequestRegister;
import ru.headsandhands.userservice.Response.AuthenticationResponse;
import ru.headsandhands.userservice.Service.Impl.AuthenticationService;
import ru.headsandhands.userservice.Service.Impl.ServiceJWTImpl;
import ru.headsandhands.userservice.Thread.ThreadLocalPayload;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ControllerUser {

    private final AuthenticationService service;
    private final ServiceJWTImpl serviceJWT;

    public ControllerUser(AuthenticationService service, ServiceJWTImpl serviceJWT) {
        this.service = service;
        this.serviceJWT = serviceJWT;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RequestRegister request
            ){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authentication(request));
    }

    @PostMapping("/refresh")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    @GetMapping("/token")
    public String printToken(@RequestHeader String token){
        ThreadLocalPayload.setId(serviceJWT.extractId(token));
        return ThreadLocalPayload.getId();
    }

}
