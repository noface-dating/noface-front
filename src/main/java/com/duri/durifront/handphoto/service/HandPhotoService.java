package com.duri.durifront.handphoto.service;

import com.duri.durifront.handphoto.dto.HandPhotoUploadResponse;
import com.duri.durifront.handphoto.exception.HandPhotoException;
import com.duri.durifront.handphoto.util.HandPhotoStorageUtil;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class HandPhotoService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp", "heic", "heif");

    private final HandPhotoAnalysisClient analysisClient;
    private final HandPhotoStorageUtil storageUtil;

    public HandPhotoUploadResponse upload(MultipartFile imageFile) {
        String ext = validateFile(imageFile);

        byte[] imageBytes = toBytes(imageFile);
        String mimeType = resolveMimeType(imageFile, ext);

        HandPhotoAnalysisClient.AnalysisResult result = analysisClient.analyze(imageBytes, mimeType);
        if (!result.allowed()) {
            throw new HandPhotoException(result.reason());
        }

        String uuid = UUID.randomUUID().toString();
        String fileName = storageUtil.save(imageBytes, uuid, ext);
        String handPhotoUrl = storageUtil.publicHandPhotoUrl(fileName);
        return new HandPhotoUploadResponse(handPhotoUrl);
    }

    private String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new HandPhotoException("손 사진을 업로드해주세요.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new HandPhotoException("파일 용량이 10MB를 초과했습니다.");
        }
        String ext = extractExt(file);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new HandPhotoException("허용되지 않는 파일 형식입니다. (jpg, jpeg, png, webp, heic, heif)");
        }
        return ext;
    }

    private String extractExt(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new HandPhotoException("파일 확장자를 확인할 수 없습니다.");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private byte[] toBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new HandPhotoException("업로드 파일을 읽을 수 없습니다.", e);
        }
    }

    private String resolveMimeType(MultipartFile file, String ext) {
        String mime = file.getContentType();
        if (mime != null && !mime.isBlank()) {
            return mime;
        }
        return switch (ext) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "webp" -> "image/webp";
            case "heic" -> "image/heic";
            case "heif" -> "image/heif";
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }
}
