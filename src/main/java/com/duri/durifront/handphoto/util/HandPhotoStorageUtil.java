package com.duri.durifront.handphoto.util;

import com.duri.durifront.handphoto.exception.HandPhotoException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HandPhotoStorageUtil {

    private final Path rootDir;
    private final Path handPhotosDir;

    public HandPhotoStorageUtil(@Value("${app.storage.root:./storage}") String root) {
        this.rootDir = Path.of(root).toAbsolutePath().normalize();
        this.handPhotosDir = rootDir.resolve("handPhotos");
        createDirectories();
    }

    public String save(byte[] data, String uuid, String ext) {
        Path target = handPhotosDir.resolve(uuid + "." + ext);
        writeBytes(target, data);
        return target.getFileName().toString();
    }

    public String publicHandPhotoUrl(String fileName) {
        return "/files/handPhotos/" + fileName;
    }

    private void createDirectories() {
        try {
            Files.createDirectories(handPhotosDir);
        } catch (IOException e) {
            throw new HandPhotoException("스토리지 디렉토리를 생성할 수 없습니다.", e);
        }
    }

    private void writeBytes(Path target, byte[] data) {
        try {
            Files.write(target, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new HandPhotoException("파일을 저장할 수 없습니다: " + target, e);
        }
    }
}
