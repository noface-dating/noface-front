package com.duri.durifront.mypage.service;

import com.duri.durifront.mypage.dto.MyPageDto;
import com.duri.durifront.profile.repository.ProfileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * {@link MyPageService}의 로컬 구현체.
 * <p>ProfileRepository를 통해 직접 DB에서 프로필을 조회·수정한다.
 * core 서버 분리 시 FeignClient 구현체로 교체된다.</p>
 */
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    @Override
    public MyPageDto getMyPage(String userId) {
        return profileRepository.findByUserUserId(userId)
                .map(profile -> MyPageDto.builder()
                        .nickname(profile.getNickname())
                        .age(Period.between(profile.getBirthDate(), LocalDate.now()).getYears())
                        .region(profile.getRegion() != null ? profile.getRegion() : "")
                        .intro(parseIntro(profile.getAdditionalInformation()))
                        .interests(parseHobbies(profile.getHobbies()))
                        .build())
                .orElse(MyPageDto.builder()
                        .nickname("게스트")
                        .age(0)
                        .region("")
                        .intro("")
                        .interests(List.of())
                        .build());
    }

    @Override
    public void updateProfile(String userId, String nickname, String region, String intro) {
        profileRepository.findByUserUserId(userId).ifPresent(profile -> {
            profile.setNickname(nickname);
            profile.setRegion(region);
            profile.setAdditionalInformation(buildAdditionalInfoJson(intro));
            profileRepository.save(profile);
        });
    }

    private String parseIntro(String additionalInfoJson) {
        if (additionalInfoJson == null || additionalInfoJson.isBlank()) return "";
        try {
            List<String> list = objectMapper.readValue(additionalInfoJson, new TypeReference<>() {});
            return list.isEmpty() ? "" : list.get(0);
        } catch (Exception e) {
            return "";
        }
    }

    private List<String> parseHobbies(String hobbiesJson) {
        if (hobbiesJson == null || hobbiesJson.isBlank()) return List.of();
        try {
            return objectMapper.readValue(hobbiesJson, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private String buildAdditionalInfoJson(String intro) {
        if (intro == null || intro.isBlank()) return "[]";
        try {
            return objectMapper.writeValueAsString(List.of(intro));
        } catch (Exception e) {
            return "[]";
        }
    }
}
