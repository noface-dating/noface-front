package com.duri.durifront.mypage.controller;

import com.duri.durifront.auth.annotation.UserId;
import com.duri.durifront.entity.Profile;
import com.duri.durifront.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final ProfileRepository profileRepository;

    @GetMapping
    public String index(@UserId String userIdStr, Model model) {
        Long userId = parseUserId(userIdStr);
        model.addAttribute("nickname", "게스트");
        model.addAttribute("age", 0);
        model.addAttribute("region", "");
        model.addAttribute("intro", "");
        model.addAttribute("interests", List.<String>of());
        profileRepository.findByUserUserId(userId).ifPresent(profile -> {
            int age = Period.between(profile.getBirthDate(), LocalDate.now()).getYears();
            model.addAttribute("nickname", profile.getNickname());
            model.addAttribute("age", age);
            model.addAttribute("region", profile.getRegion() != null ? profile.getRegion() : "");
            model.addAttribute("avatarUrl", null);
            model.addAttribute("intro", getAdditionalInfo(profile, "intro"));
            model.addAttribute("interests", hobbiestoList(profile.getHobbies()));
        });
        return "mypage/index";
    }

    @GetMapping("/edit")
    public String edit(@UserId String userIdStr, Model model) {
        Long userId = parseUserId(userIdStr);
        model.addAttribute("nickname", "");
        model.addAttribute("age", 0);
        model.addAttribute("region", "");
        model.addAttribute("intro", "");
        model.addAttribute("interests", List.<String>of());
        model.addAttribute("allTags", List.of("음악", "여행", "영화", "카페투어", "스포츠", "테니스", "맛집", "독서", "사진", "산책"));
        profileRepository.findByUserUserId(userId).ifPresent(profile -> {
            int age = Period.between(profile.getBirthDate(), LocalDate.now()).getYears();
            model.addAttribute("nickname", profile.getNickname());
            model.addAttribute("age", age);
            model.addAttribute("region", profile.getRegion() != null ? profile.getRegion() : "");
            model.addAttribute("intro", getAdditionalInfo(profile, "intro"));
            model.addAttribute("interests", hobbiestoList(profile.getHobbies()));
        });
        return "mypage/edit";
    }

    @PostMapping("/edit")
    public String editSubmit(
            @UserId String userIdStr,
            @RequestParam String nickname,
            @RequestParam String region,
            @RequestParam(required = false) String intro,
            @RequestParam(required = false) String interests) {
        Long userId = parseUserId(userIdStr);
        profileRepository.findByUserUserId(userId).ifPresent(profile -> {
            profile.setNickname(nickname);
            profile.setRegion(region);
            profile.setHobbies(listToHobbies(interests));
            Map<String, Object> info = new LinkedHashMap<>(
                    profile.getAdditionalInfo() != null ? profile.getAdditionalInfo() : Map.of());
            info.put("intro", intro != null ? intro : "");
            profile.setAdditionalInfo(info);
            profileRepository.save(profile);
        });
        return "redirect:/mypage";
    }

    private Long parseUserId(String userIdStr) {
        try {
            return Long.parseLong(userIdStr);
        } catch (Exception e) {
            return 1L;
        }
    }

    private String getAdditionalInfo(Profile profile, String key) {
        if (profile.getAdditionalInfo() == null) return "";
        Object val = profile.getAdditionalInfo().get(key);
        return val != null ? val.toString() : "";
    }

    private List<String> hobbiestoList(Map<String, Object> hobbies) {
        if (hobbies == null || hobbies.isEmpty()) return List.of();
        return List.copyOf(hobbies.keySet());
    }

    private Map<String, Object> listToHobbies(String csv) {
        if (csv == null || csv.isBlank()) return Map.of();
        Map<String, Object> map = new LinkedHashMap<>();
        Arrays.stream(csv.split(","))
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .forEach(tag -> map.put(tag, true));
        return map;
    }
}
