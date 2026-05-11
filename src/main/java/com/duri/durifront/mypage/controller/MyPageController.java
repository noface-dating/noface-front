package com.duri.durifront.mypage.controller;

import com.duri.durifront.auth.annotation.UserId;
import com.duri.durifront.auth.web.cookie.CookieService;
import com.duri.durifront.mypage.dto.MyPageDto;
import com.duri.durifront.mypage.service.MyPageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 마이페이지 컨트롤러.
 * <p>사용자 프로필 조회, 수정, 로그아웃 등 마이페이지 관련 화면을 처리한다.</p>
 */
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    @Value("${auth.server.url}")
    private String authServerUrl;

    private final MyPageService myPageService;
    private final CookieService cookieService;

    /**
     * 마이페이지 메인 화면을 반환한다.
     *
     * @param userId 로그인한 사용자 ID
     * @param model  뷰에 전달할 모델
     * @return 마이페이지 뷰 이름, 비로그인 시 로그인 페이지로 리다이렉트
     */
    @GetMapping
    public String index(@UserId String userId, Model model) {
        if (userId == null) return "redirect:/login";

        model.addAttribute("authServerUrl", authServerUrl);
        populateModel(model, myPageService.getMyPage(userId));
        return "mypage/index";
    }

    /**
     * 프로필 수정 폼 화면을 반환한다.
     *
     * @param userId 로그인한 사용자 ID
     * @param model  뷰에 전달할 모델
     * @return 프로필 수정 뷰 이름, 비로그인 시 로그인 페이지로 리다이렉트
     */
    @GetMapping("/edit")
    public String edit(@UserId String userId, Model model) {
        if (userId == null) return "redirect:/login";
        populateModel(model, myPageService.getMyPage(userId));
        return "mypage/edit";
    }

    /**
     * 프로필 수정 요청을 처리한다.
     *
     * @param userId   로그인한 사용자 ID
     * @param nickname 변경할 닉네임
     * @param region   변경할 지역
     * @param intro    변경할 자기소개 (선택)
     * @return 마이페이지로 리다이렉트, 비로그인 시 로그인 페이지로 리다이렉트
     */
    @PostMapping("/edit")
    public String editSubmit(
            @UserId String userId,
            @RequestParam String nickname,
            @RequestParam String region,
            @RequestParam(required = false) String intro) {
        if (userId == null) return "redirect:/login";
        myPageService.updateProfile(userId, nickname, region, intro);
        return "redirect:/mypage";
    }

    /**
     * 로그아웃을 처리한다.
     * <p>액세스 토큰과 리프레시 토큰 쿠키를 삭제한 뒤 로그인 페이지로 리다이렉트한다.</p>
     *
     * @param response 쿠키 삭제를 위한 HTTP 응답 객체
     * @return 로그인 페이지로 리다이렉트
     */
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        cookieService.deleteAccessTokenCookie(response);
        cookieService.deleteRefreshTokenCookie(response);
        return "redirect:/login";
    }

    private void populateModel(Model model, MyPageDto dto) {
        model.addAttribute("nickname", dto.getNickname());
        model.addAttribute("age", dto.getAge());
        model.addAttribute("region", dto.getRegion());
        model.addAttribute("intro", dto.getIntro());
        model.addAttribute("interests", dto.getInterests());
    }
}
