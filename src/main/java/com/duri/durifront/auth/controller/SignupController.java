package com.duri.durifront.auth.controller;

import com.duri.durifront.auth.dto.request.SignupUserRequestDto;
import com.duri.durifront.auth.dto.request.SignupProfileRequestDto;
import com.duri.durifront.auth.service.SignupService;
import com.duri.durifront.auth.service.SignupTempStorageService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class SignupController {

    private final SignupService signupService;
    private final SignupTempStorageService signupTempStorageService;

    @GetMapping("/signup")
    public ModelAndView getSignupUser() {
        String tempKey = UUID.randomUUID().toString();

        ModelAndView mv = new ModelAndView("/auth/signup");
        mv.addObject("tempKey", tempKey);

        return mv;
    }

    @GetMapping("/signup/profile-info")
    public ModelAndView getSignupProfileInfo(@RequestParam String tempKey) {
        ModelAndView mv = new ModelAndView("/auth/signup-profile-info");

        mv.addObject("tempKey", tempKey);

        int currentYear = LocalDate.now().getYear();

        List<Integer> years = IntStream.rangeClosed(currentYear - 79, currentYear)
                .boxed().toList();

        List<Integer> months = IntStream.rangeClosed(1, 12)
                .boxed().toList();

        List<Integer> days = IntStream.rangeClosed(1, 31)
                .boxed().toList();

        mv.addObject("years", years);
        mv.addObject("months", months);
        mv.addObject("days", days);

        return mv;
    }

    // TODO: "내 얼굴 취향 + 내 얼굴 특징 + 절대 점수" 정보 받아 사용자 & 프로필 데이터 전달 (쿼리 파라미터 활용)

    @PostMapping("/signup")
    public String signupUser(
            @RequestParam String tempKey,
            SignupUserRequestDto request
            )
    {
        signupTempStorageService.saveSignupUser(
                tempKey,
                request.username(),
                request.password(),
                request.email()
        );

        // TODO: 다음 페이지 경로 작성
        return "redirect:/?tempKey=" + tempKey;
    }

    // 프로필 기본정보
    @PostMapping("/signup/profile-info")
    public String signupProfile(
            @RequestParam String tempKey,
            SignupProfileRequestDto request
            )
    {
        signupTempStorageService.saveSignupProfile(
                tempKey,
                request.nickname(),
                request.birthDate(),
                request.gender(),
                request.region(),
                request.additionalInformation(),
                request.hobbies()
        );

        // TODO: 다음 페이지 경로 작성
        return "redirect:/?tempKey=" + tempKey;
    }

    // 최종 회원가입 완료
    // signupService.registerUserAndProfile(tempKey, facePreference, faceFeatures, absoluteScore);
    // signupTempStorageService.removeTempData(tempKey);

}
