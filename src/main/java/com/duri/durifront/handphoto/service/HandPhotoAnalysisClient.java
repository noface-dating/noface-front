package com.duri.durifront.handphoto.service;

import com.duri.durifront.handphoto.exception.HandPhotoException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class HandPhotoAnalysisClient {

    private static final String PROMPT = """
            Look at this image and decide whether it is acceptable for a "hand verification" photo.
            Rules:
            - Allowed: the visible body parts are ONLY hand(s) and/or arm(s) (forearm, upper arm, elbow, wrist). At least one hand should be clearly visible.
            - Rejected if ANY of these appear: face/head, hair as part of a face shot, neck (unless only as a tiny edge with no face), chest/torso, belly, back, legs/feet, hips, or full-body shots. Also reject if the main subject is not hands/arms (e.g. object-only, landscape, no hand).
            - Jewelry, sleeves, or background are OK as long as the rules above are met.
            Reply with exactly one JSON object, no other text: {"allowed": true or false, "reason": "one short sentence in Korean explaining why"}
            """;

    private static final String MODEL = "gemini-2.0-flash";
    private static final String GEMINI_URL_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HandPhotoAnalysisClient(
            @Value("${GEMINI_KEY:}") String envKey,
            @Value("${gemini.api.key:}") String propsKey) {
        String a = envKey != null ? envKey.trim() : "";
        if (a.isBlank() && propsKey != null && !propsKey.isBlank()) {
            a = propsKey.trim();
        }
        this.apiKey = a;
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public AnalysisResult analyze(byte[] imageBytes, String mimeType) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new HandPhotoException("Gemini API 키가 설정되지 않았습니다. GEMINI_KEY 또는 gemini.api.key를 확인하세요.");
        }

        if (imageBytes == null || imageBytes.length == 0) {
            throw new HandPhotoException("이미지 데이터가 비어 있습니다.");
        }
        String effectiveMimeType = (mimeType != null && !mimeType.isBlank())
                ? mimeType
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String requestBody = """
                {
                  "contents": [{
                    "parts": [
                      { "text": %s },
                      { "inline_data": { "mime_type": %s, "data": %s } }
                    ]
                  }],
                  "generationConfig": {
                    "temperature": 0.1
                  }
                }
                """
                .formatted(
                        quoteJson(PROMPT),
                        quoteJson(effectiveMimeType),
                        quoteJson(base64Image));

        String responseText = webClient.post()
                .uri(GEMINI_URL_TEMPLATE.formatted(MODEL, apiKey))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    String errBody = ex.getResponseBodyAsString();
                    String detail = errBody != null && !errBody.isBlank() ? (" body: " + errBody) : "";
                    return Mono.error(new HandPhotoException(
                            "손 사진 검증 실패(Gemini): " + ex.getStatusCode().value() + " " + ex.getStatusText() + detail, ex));
                })
                .onErrorResume(err -> Mono.error(new HandPhotoException("손 사진 검증 실패(Gemini): " + err.getMessage(), err)))
                .block();

        String text = extractOutputTextFromGemini(responseText);
        if (text == null || text.isBlank()) {
            throw new HandPhotoException("손 사진 검증 결과를 받지 못했습니다.");
        }
        return parseAnalysisResult(text.trim());
    }

    private AnalysisResult parseAnalysisResult(String json) {
        try {
            String raw = json;
            if (raw.startsWith("```json")) {
                raw = raw.substring(7);
            } else if (raw.startsWith("```")) {
                raw = raw.substring(3);
            }
            if (raw.endsWith("```")) {
                raw = raw.substring(0, raw.length() - 3);
            }
            raw = raw.trim();

            GeminiAnalysisResponse parsed = objectMapper.readValue(raw, GeminiAnalysisResponse.class);
            String reason = parsed.reason() != null && !parsed.reason().isBlank()
                    ? parsed.reason()
                    : "손만 보이는 사진이 아닙니다.";
            return new AnalysisResult(Boolean.TRUE.equals(parsed.allowed()), reason);
        } catch (Exception e) {
            throw new HandPhotoException("손 사진 검증 결과를 파싱할 수 없습니다.", e);
        }
    }

    private record GeminiAnalysisResponse(Boolean allowed, String reason) {}
    
    private String extractOutputTextFromGemini(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            var root = objectMapper.readTree(responseBody);
            var candidates = root.get("candidates");
            if (candidates != null && candidates.isArray()) {
                for (var candidate : candidates) {
                    var content = candidate.get("content");
                    if (content == null || !content.isObject()) {
                        continue;
                    }
                    var parts = content.get("parts");
                    if (parts == null || !parts.isArray()) {
                        continue;
                    }
                    for (var part : parts) {
                        var textNode = part.get("text");
                        if (textNode != null && textNode.isTextual() && !textNode.asText().isBlank()) {
                            return textNode.asText();
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new HandPhotoException("Gemini 손 사진 검증 응답을 파싱할 수 없습니다.", e);
        }
    }

    private String quoteJson(String s) {
        try {
            return objectMapper.writeValueAsString(s);
        } catch (Exception e) {
            throw new HandPhotoException("Gemini 요청을 만들 수 없습니다.", e);
        }
    }

    public record AnalysisResult(boolean allowed, String reason) {}
}
