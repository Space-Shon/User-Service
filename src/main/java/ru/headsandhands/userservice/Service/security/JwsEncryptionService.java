package ru.headsandhands.userservice.Service.security;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;

import java.text.ParseException;

@RequiredArgsConstructor
public class JwsEncryptionService {

    private final JWEHeader jweHeader;
    private final JWEEncrypter jweEncrypter;
    private final JWEDecrypter jweDecrypter;

    public EncryptedJWT encrypt(JWTClaimsSet claims) throws JOSEException {
        EncryptedJWT encryptedJWT = new EncryptedJWT(jweHeader, claims);
        encryptedJWT.encrypt(jweEncrypter);
        return encryptedJWT;
    }

    public JWTClaimsSet decrypt(String jwt) throws ParseException, JOSEException {
        EncryptedJWT encryptedJWT = EncryptedJWT.parse(jwt);
        encryptedJWT.decrypt(jweDecrypter);
        return encryptedJWT.getJWTClaimsSet();
    }
}
