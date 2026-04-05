package com.duri.durifront.auth.controller;

import com.duri.durifront.auth.dto.request.SignupUserRequestDto;
import com.duri.durifront.auth.dto.request.SignupProfileRequestDto;
import com.duri.durifront.auth.service.SignupService;
import com.duri.durifront.auth.service.SignupTempStorageService;
import com.duri.durifront.face_preference.dto.FacePreferenceSessionDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Controller
public class SignupController {

    private final SignupService signupService;
    private final SignupTempStorageService signupTempStorageService;
    private final ObjectMapper objectMapper;

    // 회원가입 페이지
    @GetMapping("/signup")
    public ModelAndView getSignupUser() {
        String tempKey = UUID.randomUUID().toString();

        ModelAndView mv = new ModelAndView("auth/signup");
        mv.addObject("tempKey", tempKey);
        mv.addObject("signupUserRequest",
                new SignupUserRequestDto(null, null, null));

        return mv;
    }

    // 회원가입 - 프로필 정보 페이지
    @GetMapping("/signup/profile-info")
    public ModelAndView getSignupProfileInfo(@RequestParam String tempKey) {
        ModelAndView mv = new ModelAndView("auth/signup-profile-info");

        mv.addObject("tempKey", tempKey);
        mv.addObject("signupProfileRequest",
                new SignupProfileRequestDto(null, null, null,
                        null, null, null));

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

    // 회원가입 - 아이디 중복 조회
    @GetMapping("/signup/check-username")
    @ResponseBody
    public boolean checkUsernameDuplicated(@RequestParam String username) {
        return signupService.isUsernameDuplicated(username);
    }

    // 회원가입 : 사용자 생성
    @PostMapping("/signup")
    public ModelAndView signupUser(
            @RequestParam String tempKey,
            @Valid SignupUserRequestDto request,
            BindingResult bindingResult
            )
    {
        // 아이디 중복 체크
        if (signupService.isUsernameDuplicated(request.username())) {
            bindingResult.rejectValue(
                    "username",
                    "duplicate",
                    "이미 사용 중인 아이디입니다."
            );
        }

        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("auth/signup");

            mv.addObject("tempKey", tempKey);
            mv.addObject("signupUserRequest", request);

            return mv;
        }

        signupTempStorageService.saveSignupUser(
                tempKey,
                request.username(),
                request.password(),
                request.email()
        );

        return new ModelAndView("redirect:/signup/profile-info?tempKey=" + tempKey);
    }

    // 회원가입 - 프로필 기본정보 + 최종 완료
    @PostMapping("/signup/profile-info")
    public ModelAndView signupProfile(
            @RequestParam String tempKey,
            @Valid SignupProfileRequestDto request,
            BindingResult bindingResult,
            @RequestParam(required = false) String additionalInformation,
            @RequestParam(required = false) String hobbies,
            HttpSession session
            )
    {
        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("auth/signup-profile-info");

            mv.addObject("tempKey", tempKey);
            mv.addObject("signupProfileRequest", request);

            int currentYear = LocalDate.now().getYear();
            mv.addObject("years", IntStream.rangeClosed(currentYear - 79, currentYear).boxed().toList());
            mv.addObject("months", IntStream.rangeClosed(1, 12).boxed().toList());
            mv.addObject("days", IntStream.rangeClosed(1, 31).boxed().toList());

            return mv;
        }

        signupTempStorageService.saveSignupProfile(
                tempKey,
                request.nickname(),
                request.birthDate(),
                request.gender(),
                request.region(),
                parseJsonList(additionalInformation),
                parseJsonList(hobbies)
        );

        // 세션에서 취향 퀴즈 결과 가져오기 (없으면 기본값)
        FacePreferenceSessionDto quizResult =
                (FacePreferenceSessionDto) session.getAttribute("facePreferenceResult");
        String facePreference = (quizResult != null) ? quizResult.getDescriptionKey() : "0000000000";

        signupService.registerUserAndProfile(tempKey, facePreference, "0000000000", (byte) 50);
        signupTempStorageService.removeTempData(tempKey);

        return new ModelAndView("redirect:/login");
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return json.isBlank() ? List.of() : List.of(json);
        }
    }
}
