package com.duri.durifront.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    /**
     * public-R2에 파일을 업로드하는 메소드입니다.
     *
     * @param dir 업로드 할 위치
     * @param file 업로드 할 파일
     * @return 파일경로 및 이름
     * @throws IOException
     */
    public String upload(String dir, MultipartFile file) throws IOException {
        String key;
        key = "%s/%s.%s".formatted(dir, UUID.randomUUID(), Objects.requireNonNull(file
                        .getOriginalFilename())
                .substring(
                        file.getOriginalFilename().lastIndexOf(".") + 1
                ));

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return key;
    }

    /**
     * 해당 파일 업로드 링크
     *
     * @param key 디렉토리 + 파일 이름
     * @return 업로드 링크와 {@code key }
     */
    public Map<String, String> createUploadPresignedUrl(String key) {
        final Map<String, String> contentTypeMap = Map.of(
                "jpg", "image/jpeg",
                "jpeg", "image/jpeg",
                "png", "image/png",
                "webp", "image/webp",
                "gif", "image/gif",
                "mp4", "video/mp4",
                "mov", "video/quicktime"
        );

        String fileType = key.substring(key.lastIndexOf(".") + 1);
        String contentType = Optional.ofNullable(contentTypeMap.get(fileType)).orElse("application/octet-stream");
        if (contentType.equals("application/octet-stream")) {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        return Map.of(
                "url", presignedRequest.url().toString(),
                "key", key
        );
    }

    /**
     * 일정 시간동안 파일을 열람할 수 있는 임시 URL
     * 기간: 7일
     *
     * @param key 디렉토리 + 파일 이름
     * @return 해당 파일에 대한 임시 URL
     */
    public String createDownloadPresignedUrl(String key) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofDays(7))
                        .getObjectRequest(getObjectRequest)
                        .build();

        PresignedGetObjectRequest presignedRequest =
                s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
