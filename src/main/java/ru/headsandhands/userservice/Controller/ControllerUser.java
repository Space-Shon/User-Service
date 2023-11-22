package ru.headsandhands.userservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.headsandhands.userservice.Request.AuthenticationRequest;
import ru.headsandhands.userservice.Request.RequestRegister;
import ru.headsandhands.userservice.Response.AuthenticationResponse;
import ru.headsandhands.userservice.Service.Impl.AuthenticationService;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ControllerUser {

    private final AuthenticationService service;

    public ControllerUser(AuthenticationService service) {
        this.service = service;
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

}
