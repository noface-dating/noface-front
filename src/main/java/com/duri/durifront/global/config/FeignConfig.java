package com.duri.durifront.global.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Configuration
public class FeignConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // TODO JWT 구현 완료되면 로직 작성 완료 할 것.
            requestTemplate.header("X-TEST-HEADER", "1");
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }
}
