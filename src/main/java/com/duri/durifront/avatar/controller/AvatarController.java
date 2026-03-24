package com.duri.durifront.avatar.controller;

import com.duri.durifront.avatar.dto.AvatarGenerateResponse;
import com.duri.durifront.avatar.service.AvatarService;
import com.duri.durifront.avatar.service.AvatarService.AvatarResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateAvatar(
            @RequestPart("face") MultipartFile face,
            @RequestHeader(value = HttpHeaders.ACCEPT, required = false) String acceptHeader) {

        var responseType = avatarService.detectResponseType(acceptHeader);
        AvatarResult result = avatarService.generate(face);

        if (MediaType.IMAGE_PNG.equals(responseType)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_LOCATION, result.avatarUrl())
                    .body(result.avatarBytes());
        }

        return ResponseEntity.ok(new AvatarGenerateResponse(result.avatarUrl()));
    }
}
