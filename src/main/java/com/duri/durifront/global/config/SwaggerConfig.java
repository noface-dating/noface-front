package com.duri.durifront.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger(SpringDoc OpenAPI) 설정 클래스.
 * <p>Swagger UI 접속 경로: {@code /swagger-ui/index.html}</p>
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NoFace Front API")
                        .description("마이페이지 및 취향 이지선다 관련 API 문서")
                        .version("v1.0.0"));
    }
}
