package com.duri.durifront.avatar.service;

import com.duri.durifront.avatar.exception.AvatarException;
import com.duri.durifront.avatar.util.ImageToPngConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * OpenAI <strong>Images</strong> API — {@code POST /v1/images/edits}.
 * <ul>
 *   <li><strong>gpt-image-1</strong> (기본): 카툰 변환 품질에 유리, multipart 필드 {@code image[]}</li>
 *   <li><strong>dall-e-2</strong>: 레거시, 필드 {@code image}, 결과가 사진에 가깝게 나오는 경우 많음</li>
 * </ul>
 * 설정: {@code openai.avatar.edit-model}
 */
@Component
public class OpenAIAvatarClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAIAvatarClient.class);

    private static final String DEFAULT_EDIT_MODEL = "gpt-image-1";
    private static final String LEGACY_MODEL = "dall-e-2";
    private static final String EDIT_URL = "https://api.openai.com/v1/images/edits";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String PROMPT = """
            Completely redraw this person as a stylized 3D cartoon character head-and-shoulders portrait with a pure white background (#FFFFFF), not a photograph.
            Pixar / DreamWorks-style 3D render: smooth CG skin, simplified but faithful facial structure, expressive eyes, clean studio lighting.
            Preserve identity as much as possible: face shape, eyes, brows, nose, lips, jawline, cheekbones, ears, hairline, hairstyle, and hair part.
            Keep skin tone and age impression close to the original; no realistic pores, no DSLR look, no background objects or scenery.
            No text, logo, or watermark. Single character only, centered, facing forward.
            """.stripIndent();

    private final WebClient webClient;
    private final String apiKey;
    private final Environment environment;

    public OpenAIAvatarClient(Environment environment) {
        this.environment = environment;
        // @Value 는 spring.config.import 된 키를 놓치는 경우가 있어 Environment 로 통일
        String a = firstNonBlank(
                environment.getProperty("OPENAI_KEY"),
                environment.getProperty("openai.api.key"));
        this.apiKey = a != null ? a : "";
        this.webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    private static String firstNonBlank(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String s : candidates) {
            if (s != null && !s.isBlank()) {
                return s.trim();
            }
        }
        return null;
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    public byte[] generateAvatar(byte[] faceImage, String mimeType) {
        if (this.apiKey.isBlank()) {
            throw new AvatarException("OpenAI API 키가 설정되지 않았습니다. OPENAI_KEY 또는 application-secret.properties를 확인하세요.");
        }

        byte[] pngFace = ImageToPngConverter.convertToOpenAiPng(faceImage);
        String editModel = firstNonBlank(
                environment.getProperty("openai.avatar.edit-model"),
                DEFAULT_EDIT_MODEL);
        boolean useLegacyDalle2 = LEGACY_MODEL.equalsIgnoreCase(editModel.trim());

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("model", editModel.trim());
        builder.part("prompt", PROMPT);
        builder.part("size", "1024x1024");

        ByteArrayResource facePart = new ByteArrayResource(pngFace) {
            @Override
            public String getFilename() {
                return "face.png";
            }
        };

        if (useLegacyDalle2) {
            builder.part("image", facePart).contentType(MediaType.IMAGE_PNG);
            builder.part("response_format", "b64_json");
        } else {
            // GPT Image 계열: 공식 예시는 multipart 필드 image[]
            builder.part("image[]", facePart).contentType(MediaType.IMAGE_PNG);
            builder.part("output_format", "png");
            builder.part("input_fidelity", "high");
            builder.part("quality", "medium");
            builder.part("n", "1");
        }

        log.info(
                "OpenAI images/edits: model={}, dallE2Legacy={}, pngInputBytes={}",
                editModel.trim(),
                useLegacyDalle2,
                pngFace.length);

        byte[] result = webClient.post()
                .uri(EDIT_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchangeToMono(response -> response.bodyToMono(String.class).defaultIfEmpty("").flatMap(body -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        String detail = body.isBlank() ? "" : " " + body.substring(0, Math.min(400, body.length()));
                        return Mono.<byte[]>error(new AvatarException(
                                "OpenAI 이미지 API 오류 (" + response.statusCode().value() + "):" + detail));
                    }
                    try {
                        JsonNode root = OBJECT_MAPPER.readTree(body);
                        JsonNode data = root != null ? root.get("data") : null;
                        if (data == null || !data.isArray() || data.isEmpty()) {
                            return Mono.error(new AvatarException(
                                    "이미지가 생성되지 않았습니다. 다른 사진으로 시도해 보세요."));
                        }
                        JsonNode first = data.get(0);
                        String b64 = first.has("b64_json") && first.get("b64_json").isTextual()
                                ? first.get("b64_json").asText()
                                : null;
                        if (b64 != null && !b64.isBlank()) {
                            return Mono.just(Base64.getDecoder().decode(b64.getBytes(StandardCharsets.UTF_8)));
                        }
                        if (first.has("url") && first.get("url").isTextual()) {
                            String imageUrl = first.get("url").asText();
                            return WebClient.create()
                                    .get()
                                    .uri(imageUrl)
                                    .retrieve()
                                    .bodyToMono(byte[].class);
                        }
                        return Mono.error(new AvatarException(
                                "이미지가 생성되지 않았습니다. 다른 사진으로 시도해 보세요."));
                    } catch (Exception e) {
                        return Mono.error(new AvatarException(
                                "OpenAI 응답 형식 오류. " + e.getMessage(), e));
                    }
                }))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    String body = ex.getResponseBodyAsString();
                    String detail = body != null && !body.isBlank() ? (" " + body.substring(0, Math.min(400, body.length()))) : "";
                    return Mono.error(new AvatarException("OpenAI API 오류 (" + ex.getStatusCode().value() + "):" + detail, ex));
                })
                .onErrorResume(AvatarException.class, Mono::error)
                .onErrorResume(err -> Mono.error(new AvatarException(
                        err instanceof AvatarException ? err.getMessage() : "OpenAI 호출 실패. " + err.getMessage(), err)))
                .block();

        if (result == null) {
            throw new AvatarException("이미지가 생성되지 않았습니다. 다른 사진으로 시도해 보세요.");
        }
        return result;
    }
}
