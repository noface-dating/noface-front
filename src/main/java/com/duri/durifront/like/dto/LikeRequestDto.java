package com.duri.durifront.like.dto;

public record LikeRequestDto(Long fromUserId, Long toUserId, String type) {
}
