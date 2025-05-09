package ru.headsandhands.userservice.Controller;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;
import ru.headsandhands.userservice.Request.AuthenticationRequest;
import ru.headsandhands.userservice.Request.RequestRegister;
import ru.headsandhands.userservice.Response.AuthenticationResponse;
import ru.headsandhands.userservice.Service.Impl.AuthenticationService;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ControllerUser {

    private LoggerFactory loggerFactory;
    private final AuthenticationService service;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RequestRegister request
    ) throws JOSEException {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ) throws JOSEException {
        return ResponseEntity.ok(service.authentication(request));
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public HttpStatus delete(@PathVariable Integer id) {
        service.deleteUser(id);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send("user-topic", id.toString());

        future.whenComplete((result, ex) ->
                {
                    if (ex == null)
                        log.info("User delete succesfully and kafka message sent in " + result.getRecordMetadata().partition() + " partition");
                    else
                        log.error("User delete failed", ex);
                }
        );
        return HttpStatus.OK;
    }
}
