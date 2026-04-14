package com.duri.durifront.mypage.controller;

import com.duri.durifront.auth.annotation.UserId;
import com.duri.durifront.auth.web.cookie.CookieService;
import com.duri.durifront.profile.entity.Profile;
import com.duri.durifront.profile.repository.ProfileRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    @Value("${auth.server.url}")
    private String authServerUrl;

    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;
    private final CookieService cookieService;

    @GetMapping
    public String index(@UserId String userId, Model model) {
        if (userId == null) return "redirect:/login";

        model.addAttribute("authServerUrl", authServerUrl);

        setDefaults(model);
        profileRepository.findByUserUserId(userId).ifPresent(p -> populateModel(model, p));
        return "mypage/index";
    }

    @GetMapping("/edit")
    public String edit(@UserId String userId, Model model) {
        if (userId == null) return "redirect:/login";
        setDefaults(model);
        profileRepository.findByUserUserId(userId).ifPresent(p -> populateModel(model, p));
        return "mypage/edit";
    }

    @PostMapping("/edit")
    public String editSubmit(
            @UserId String userId,
            @RequestParam String nickname,
            @RequestParam String region,
            @RequestParam(required = false) String intro) {
        if (userId == null) return "redirect:/login";
        profileRepository.findByUserUserId(userId).ifPresent(profile -> {
            profile.setNickname(nickname);
            profile.setRegion(region);
            profile.setAdditionalInformation(buildAdditionalInfoJson(intro));
            profileRepository.save(profile);
        });
        return "redirect:/mypage";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        cookieService.deleteAccessTokenCookie(response);
        cookieService.deleteRefreshTokenCookie(response);
        return "redirect:/login";
    }

    private void setDefaults(Model model) {
        model.addAttribute("nickname", "게스트");
        model.addAttribute("age", 0);
        model.addAttribute("region", "");
        model.addAttribute("intro", "");
        model.addAttribute("interests", List.<String>of());
    }

    private void populateModel(Model model, Profile profile) {
        int age = Period.between(profile.getBirthDate(), LocalDate.now()).getYears();
        model.addAttribute("nickname", profile.getNickname());
        model.addAttribute("age", age);
        model.addAttribute("region", profile.getRegion() != null ? profile.getRegion() : "");
        model.addAttribute("intro", parseIntro(profile.getAdditionalInformation()));
        model.addAttribute("interests", parseHobbies(profile.getHobbies()));
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
