package com.duri.durifront.auth.provider;

import com.duri.durifront.auth.common.properties.JwtProperties;
import com.duri.durifront.auth.exception.logging.JwtKeyInitializationException;
import jakarta.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class JwtKeyProvider {

    private final JwtProperties jwtProperties;

    private ECPublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");

            // Public Key
            byte[] publicBytes = Base64.getDecoder()
                    .decode(this.normalize(jwtProperties.getPublicKey()));
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);
            this.publicKey = (ECPublicKey) keyFactory.generatePublic(publicSpec);

        } catch (Exception e) {
            throw new JwtKeyInitializationException("PEM Key 로딩 실패", e);
        }
    }

    private String normalize(String key) {
        return key.replaceAll("\\s", "");
    }
}
