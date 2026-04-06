package com.duri.durifront.avatar.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final Path storageRootAbsolute;

    public StaticResourceConfig(@Value("${app.storage.root:./storage}") String storageRoot) {
        // FileStorageUtil 과 동일: ./storage → user.dir 기준 절대 경로 (file:./storage 와 불일치 방지)
        this.storageRootAbsolute = Path.of(storageRoot).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = storageRootAbsolute.toUri().toString();
        String withSlash = location.endsWith("/") ? location : location + "/";
        registry.addResourceHandler("/files/**")
                .addResourceLocations(withSlash);
        // UI: Thymeleaf + classpath:/static (React dist 미사용)
        // 임시: 손사진 테스트 페이지 (테스트 후 삭제 대상)
        registry.addResourceHandler("/hand-photo-test/**")
                .addResourceLocations("file:./hand-photo-test/");
        // 임시: 아바타 생성 테스트 페이지
        registry.addResourceHandler("/avatar-test/**")
                .addResourceLocations("file:./avatar-test/");
    }
}

//생성 오류 해결됨
