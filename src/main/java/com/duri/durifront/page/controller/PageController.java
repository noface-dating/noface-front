package com.duri.durifront.page.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * HIDE 프론트엔드 페이지 라우팅 컨트롤러.
 * 모든 페이지를 Thymeleaf 뷰로 매핑합니다.
 */
@Controller
public class PageController {

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getPrincipal() instanceof String)
                ? (String) auth.getPrincipal()
                : "";
    }

    // ── 온보딩 ──
    @GetMapping("/onboarding")
    public String onboarding() { return "onboarding/onboarding"; }

    @GetMapping("/onboarding/find-type")
    public String findType() { return "onboarding/find-type"; }

    @GetMapping("/onboarding/face-preference")
    public String facePreference() { return "onboarding/face-preference"; }

    @GetMapping("/onboarding/preference-result")
    public String preferenceResult() { return "onboarding/preference-result"; }

    // ── 인증 ──
    // 인증 관련 라우팅은 auth 컨트롤러에서 처리

    // ── 셋업 ──
    @GetMapping("/setup/avatar")
    public String setupAvatar() { return "setup/avatar-creation"; }

    @GetMapping("/setup/hand")
    public String setupHand() { return "setup/hand-verification"; }

    @GetMapping("/setup/interests")
    public String setupInterests() { return "setup/interests"; }

    @GetMapping("/setup/profile")
    public String setupProfile() { return "setup/profile-setup"; }

    @GetMapping("/setup/discovery")
    public String setupDiscovery() { return "setup/discovery-preferences"; }

    // ── 메인 앱 ──
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("userId", getCurrentUserId());
        return "main/home";
    }

    @GetMapping("/messages")
    public String messages() { return "main/messages"; }

    @GetMapping("/messages/{chatId}")
    public String messageChat(@PathVariable String chatId) { return "main/message-chat"; }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("userId", getCurrentUserId());
        return "main/profile";
    }

    @GetMapping("/profile/{userId}")
    public String profileView(@PathVariable String userId, Model model) {
        model.addAttribute("currentUserId", getCurrentUserId());
        return "main/profile-view";
    }

    @GetMapping("/community")
    public String community() { return "main/community"; }

    // ── 서브 페이지 ──
    @GetMapping("/likes")
    public String likes(Model model) {
        model.addAttribute("userId", getCurrentUserId());
        return "sub/likes";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        model.addAttribute("userId", getCurrentUserId());
        return "sub/edit-profile";
    }

    @GetMapping("/tier-missions")
    public String tierMissions(Model model) {
        model.addAttribute("userId", getCurrentUserId());
        return "sub/tier-missions";
    }

    @GetMapping("/support")
    public String support() { return "sub/customer-support"; }

    @GetMapping("/support/help")
    public String supportHelp() { return "sub/support-help"; }

    @GetMapping("/support/report")
    public String supportReport() { return "sub/support-report"; }

    @GetMapping("/support/inquiry")
    public String supportInquiry() { return "sub/support-inquiry"; }
}
