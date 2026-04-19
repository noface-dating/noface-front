package com.duri.durifront.handphoto.controller;

import com.duri.durifront.handphoto.dto.HandPhotoUploadResponse;
import com.duri.durifront.handphoto.service.HandPhotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.duri.durifront.auth.annotation.UserId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/hand-photo")
public class HandPhotoController {

    private final HandPhotoService handPhotoService;

    public HandPhotoController(HandPhotoService handPhotoService) {
        this.handPhotoService = handPhotoService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HandPhotoUploadResponse> upload(
            @RequestPart("image") MultipartFile image,
            @UserId String userId) {
        HandPhotoUploadResponse response = handPhotoService.upload(image, userId);
        return ResponseEntity.ok(response);
    }
}
