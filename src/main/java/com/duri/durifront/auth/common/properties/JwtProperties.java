package com.duri.durifront.auth.common.properties;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String issuer;
    private String publicKey;
    private AccessToken access = new AccessToken();

    @Setter
    @Getter
    public static class AccessToken {
        private Duration validity;
    }

}
