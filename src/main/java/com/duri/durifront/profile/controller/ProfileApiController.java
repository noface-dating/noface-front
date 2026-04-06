package com.duri.durifront.profile.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duri.durifront.profile.dto.ProfileRecommendationDto;
import com.duri.durifront.profile.service.ProfileRecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileApiController {

	private final ProfileRecommendationService recommendationService;

	@GetMapping("/recommendations")
	public ResponseEntity<List<ProfileRecommendationDto>> getRecommendations(
			@RequestParam(defaultValue = "") String userId,
			@RequestParam(defaultValue = "0") int page) {
		if (userId.isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		List<ProfileRecommendationDto> recommendations = recommendationService.getRecommendedProfiles(userId, page);
		return ResponseEntity.ok(recommendations);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getProfileDetail(@PathVariable String userId) {
		try {
			if ("undefined".equals(userId) || "null".equals(userId) || userId.isBlank()) {
				return ResponseEntity.badRequest().body("Error: Received invalid userId from frontend: " + userId);
			}
			com.duri.durifront.profile.dto.ProfileDetailDto detail = recommendationService.getProfileDetail(userId);
			return ResponseEntity.ok(detail);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error fetching profile: " + e.getMessage());
		}
	}

}
