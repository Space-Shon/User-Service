package ru.headsandhands.userservice.Config.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.headsandhands.userservice.Config.property.JwtProperties;
import ru.headsandhands.userservice.Service.security.JwsEncryptionService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@Configuration
public class JwtEncryptionConfig {

    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    private static final JcaPEMKeyConverter pemKeyConverter = new JcaPEMKeyConverter();

    @Bean
    JwsEncryptionService rsaJwtEncryptionService(JwtProperties jwtProperties) throws IOException {
        return new JwsEncryptionService(
                newJWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128GCM, JOSEObjectType.JWT),
                new RSAEncrypter(getRSAPublicKey(jwtProperties.publicKey())),
                new RSADecrypter(getRSAPrivateKey(jwtProperties.privateKey()))
        );
    }

    private RSAPublicKey getRSAPublicKey(String publicKey) throws IOException {
        PEMParser pemParser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(base64Decoder.decode(publicKey))));
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
        return (RSAPublicKey) pemKeyConverter.getPublicKey(publicKeyInfo);
    }

    private RSAPrivateKey getRSAPrivateKey(String privateKey) throws IOException {
        PEMParser pemParser = new PEMParser(new InputStreamReader(new ByteArrayInputStream(base64Decoder.decode(privateKey))));
        PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
        return (RSAPrivateKey) pemKeyConverter.getPrivateKey(pemKeyPair.getPrivateKeyInfo());
    }

    private JWEHeader newJWEHeader(
            JWEAlgorithm jweAlgorithm,
            EncryptionMethod encryptionMethod,
            JOSEObjectType joseObjectType
    ) {
        return new JWEHeader(
                jweAlgorithm, encryptionMethod, joseObjectType,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, 0, null, null, null,
                null, null
        );
    }
}
