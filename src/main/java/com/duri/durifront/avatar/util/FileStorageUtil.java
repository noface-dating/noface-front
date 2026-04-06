package com.duri.durifront.avatar.util;

import com.duri.durifront.avatar.exception.AvatarException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStorageUtil {

    private final Path rootDir;
    private final Path facesDir;
    private final Path avatarsDir;

    public FileStorageUtil(@Value("${app.storage.root:./storage}") String root) {
        this.rootDir = Path.of(root).toAbsolutePath().normalize();
        this.facesDir = rootDir.resolve("faces");
        this.avatarsDir = rootDir.resolve("avatars");
        createDirectories();
    }

    public String saveFace(MultipartFile file) {
        String ext = resolveExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        Path target = facesDir.resolve(uuid + "." + ext);
        writeBytes(target, bytes(file));
        return target.getFileName().toString();
    }

    public String saveAvatar(byte[] pngBytes, String uuid) {
        Path target = avatarsDir.resolve(uuid + ".png");
        writeBytes(target, pngBytes);
        return target.getFileName().toString();
    }

    /** OpenAI 미사용(데모) 시 업로드 원본 바이트를 아바타 폴더에 그대로 저장 */
    public String saveUploadedAsAvatar(byte[] data, String uuid, String ext) {
        String safe = ext == null || ext.isBlank() ? "jpg" : ext.toLowerCase();
        if ("jpeg".equals(safe)) {
            safe = "jpg";
        }
        Path target = avatarsDir.resolve(uuid + "." + safe);
        writeBytes(target, data);
        return target.getFileName().toString();
    }

    public String publicAvatarUrl(String avatarFileName) {
        return "/files/avatars/" + avatarFileName;
    }

    public Path getRootDir() {
        return rootDir;
    }

    private void createDirectories() {
        try {
            Files.createDirectories(facesDir);
            Files.createDirectories(avatarsDir);
        } catch (IOException e) {
            throw new AvatarException("스토리지 디렉토리를 생성할 수 없습니다.", e);
        }
    }

    private byte[] bytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new AvatarException("업로드 파일을 읽을 수 없습니다.", e);
        }
    }

    private void writeBytes(Path target, byte[] data) {
        try {
            Files.write(target, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new AvatarException("파일을 저장할 수 없습니다: " + target, e);
        }
    }

    private String resolveExtension(String filename) {
        String ext = StringUtils.getFilenameExtension(filename);
        if (ext == null || ext.isBlank()) {
            throw new AvatarException("확장자를 찾을 수 없습니다.");
        }
        return ext.toLowerCase();
    }
}
