package com.duri.durifront.avatar.service;

import com.duri.durifront.avatar.entity.Avatar;
import com.duri.durifront.avatar.entity.AvatarId;
import com.duri.durifront.avatar.exception.AvatarException;
import com.duri.durifront.avatar.repository.AvatarRepository;
import com.duri.durifront.avatar.util.FileStorageUtil;
import com.duri.durifront.profile.entity.Profile;
import com.duri.durifront.profile.repository.ProfileRepository;
import com.duri.durifront.user.entity.User;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 얼굴 업로드 → <strong>OpenAI Images API</strong> ({@code POST /v1/images/edits}, {@code dall-e-2}) 로 아바타 생성.
 * React({@code /api/avatar/generate})·Thymeleaf({@code POST /setup/avatar}) 모두 이 서비스만 사용한다.
 * {@link GeminiAvatarClient} 는 프로젝트에 있으나 아바타 플로우에는 연결하지 않는다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AvatarService {

    private static final Logger log = LoggerFactory.getLogger(AvatarService.class);

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    private final OpenAIAvatarClient openAIAvatarClient;
    private final FileStorageUtil fileStorageUtil;
    private final ProfileRepository profileRepository;
    private final AvatarRepository avatarRepository;

    /**
     * true면 OpenAI 키가 없을 때 업로드 원본을 그대로 아바타로 저장(로컬 UI 데모용).
     * 기본 false — 키 없으면 오류를 내 실제 생성 실패를 숨기지 않음.
     */
    @Value("${avatar.fallback-original-without-api:false}")
    private boolean fallbackOriginalWithoutApi;

    public AvatarResult generate(MultipartFile faceFile, String userId) {
        String ext = validateFile(faceFile);

        byte[] faceBytes = toBytes(faceFile);
        String mimeType = resolveMimeType(faceFile, ext);

        // Save original face
        fileStorageUtil.saveFace(faceFile);

        String uuid = UUID.randomUUID().toString();

        String avatarUrl;
        byte[] avatarBytes;

        if (!openAIAvatarClient.hasApiKey()) {
            log.warn("OpenAI API key missing — set OPENAI_KEY or openai.api.key (see application-secret.properties)");
            if (fallbackOriginalWithoutApi) {
                log.warn("avatar.fallback-original-without-api=true → saving uploaded photo as avatar (no AI)");
                String avatarFileName = fileStorageUtil.saveUploadedAsAvatar(faceBytes, uuid, ext);
                avatarUrl = fileStorageUtil.publicAvatarUrl(avatarFileName);
                avatarBytes = faceBytes;
            } else {
                throw new AvatarException(
                        "OpenAI API 키가 없어 AI 아바타를 만들 수 없어요. "
                                + "터미널에서 OPENAI_KEY를 설정하거나 noface-front/application-secret.properties에 "
                                + "openai.api.key=... 를 넣은 뒤 서버를 다시 시작해 주세요.");
            }
        } else {
            log.info("Calling OpenAI Images /edits for avatar (input {} bytes, mime={})", faceBytes.length, mimeType);
            avatarBytes = openAIAvatarClient.generateAvatar(faceBytes, mimeType);
            String avatarFileName = fileStorageUtil.saveAvatar(avatarBytes, uuid);
            avatarUrl = fileStorageUtil.publicAvatarUrl(avatarFileName);
            log.info("Avatar saved: {} ({} bytes PNG)", avatarUrl, avatarBytes.length);
        }

        Profile profile = profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new AvatarException("프로필이 존재하지 않습니다."));
        User user = profile.getUser();

        AvatarId id = new AvatarId(profile.getProfileId(), user.getUserId());
        Avatar avatar = Avatar.builder()
                .id(id)
                .profile(profile)
                .user(user)
                .avatarImageUrl(avatarUrl)
                .createdAt(LocalDateTime.now())
                .build();
        avatarRepository.save(avatar);

        return new AvatarResult(avatarUrl, avatarBytes);
    }

    private String validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AvatarException("얼굴 이미지를 업로드해주세요.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AvatarException("파일 용량이 10MB를 초과했습니다.");
        }
        String ext = extractExt(file);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new AvatarException("허용되지 않는 파일 형식입니다. (jpg, jpeg, png, webp)");
        }
        return ext;
    }

    private String extractExt(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        }
        String ct = file.getContentType();
        if (ct != null) {
            String lower = ct.toLowerCase();
            if (lower.contains("jpeg")) {
                return "jpg";
            }
            if (lower.contains("png")) {
                return "png";
            }
            if (lower.contains("webp")) {
                return "webp";
            }
        }
        throw new AvatarException("파일 형식을 확인할 수 없습니다. jpg, png, webp 이미지를 선택해 주세요.");
    }

    private byte[] toBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new AvatarException("업로드 파일을 읽을 수 없습니다.", e);
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
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }

    public MediaType detectResponseType(String acceptHeader) {
        if (acceptHeader != null && acceptHeader.toLowerCase().contains(MediaType.IMAGE_PNG_VALUE)) {
            return MediaType.IMAGE_PNG;
        }
        return MediaType.APPLICATION_JSON;
    }

    public record AvatarResult(String avatarUrl, byte[] avatarBytes) {}
}
