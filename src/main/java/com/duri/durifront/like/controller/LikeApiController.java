package com.duri.durifront.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duri.durifront.like.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeApiController {

    private final LikeService likeService;

    private final com.duri.durifront.like.service.PassService passService;

    public record LikeRequest(Long fromUserId, Long toUserId, String type) {}

    @PostMapping
    public ResponseEntity<?> sendLike(@RequestBody LikeRequest request) {
        if ("DISLIKE".equalsIgnoreCase(request.type())) {
            passService.sendPass(request.fromUserId(), request.toUserId());
            return ResponseEntity.ok().build();
        }
        
        LikeService.LikeResult result = likeService.sendLike(request.fromUserId(), request.toUserId());
        return ResponseEntity.ok(result);
    }
}
