package ru.headsandhands.userservice.Service.security;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.headsandhands.userservice.Config.property.JwtProperties;
import ru.headsandhands.userservice.Model.Role;
import ru.headsandhands.userservice.Model.User;
import ru.headsandhands.userservice.Model.jwt.JwtPayload;
import ru.headsandhands.userservice.Model.jwt.JwtToken;
import ru.headsandhands.userservice.Model.jwt.JwtTokenPair;
import ru.headsandhands.userservice.Service.JWTService;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class ServiceJWTImpl implements JWTService {

    private final static String ROLE = "role";
    private final static String IS_ACCESS_TOKEN = "isAccessToken";

    private final JwtProperties jwtProperties;
    private final JwsEncryptionService jwtEncryptionService;

    @Autowired
    public ServiceJWTImpl(JwtProperties jwtProperties, JwsEncryptionService jwtEncryptionService) {
        this.jwtProperties = jwtProperties;
        this.jwtEncryptionService = jwtEncryptionService;
    }

    @Override
    public JwtTokenPair buildJwtTokenPair(User user, Role role) throws JOSEException {
        return new JwtTokenPair(
                encryptJwtToken(user, jwtProperties.accessTokenTtl(), true, role),
                encryptJwtToken(user, jwtProperties.refreshTokenTtl(), false, role)
        );
    }

    @Override
    public JwtToken parseJwtToken(String token) {
        try {
            return jwtToken(jwtEncryptionService.decrypt(token));
        } catch (Exception ex) {
            throw new IllegalStateException("JWT token verification failed: " + token, ex);
        }
    }

    private JwtToken jwtToken(JWTClaimsSet jwtClaimsSet) throws ParseException {
        JwtPayload jwtPayload = new JwtPayload(
                Long.valueOf(jwtClaimsSet.getSubject()),
                Role.valueOf((String) jwtClaimsSet.getClaim(ROLE)),
                LocalDateTime.ofInstant(jwtClaimsSet.getExpirationTime().toInstant(), ZoneOffset.UTC)
        );
        Boolean isAccessToken = jwtClaimsSet.getBooleanClaim(IS_ACCESS_TOKEN);
        if (isAccessToken == null) {
            throw new IllegalStateException("JWT token verification failed");
        }

        if (isAccessToken)
            return new JwtToken.AccessToken(jwtPayload);
        else
            return new JwtToken.RefreshToken(jwtPayload);
    }

    private String encryptJwtToken(User user, Long ttl, Boolean isAccessToken, Role role) throws JOSEException {
        return jwtEncryptionService.encrypt(new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .claim(ROLE, role)
                .claim(IS_ACCESS_TOKEN, isAccessToken)
                .issueTime(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)))
                .expirationTime(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC).plusSeconds(ttl)))
                .build()).serialize();

    }
}
