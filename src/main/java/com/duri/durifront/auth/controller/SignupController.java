package com.duri.durifront.auth.controller;

import com.duri.durifront.auth.dto.request.SignupStep01RequestDto;
import com.duri.durifront.auth.dto.request.SignupStep02RequestDto;
import com.duri.durifront.auth.dto.request.SignupStep03RequestDto;
import com.duri.durifront.auth.dto.request.SignupStep04RequestDto;
import com.duri.durifront.auth.dto.request.SignupStep05RequestDto;
import com.duri.durifront.auth.service.SignupService;
import com.duri.durifront.auth.service.SignupTempStorageService;
import com.duri.durifront.profile.service.ProfileService;
import com.duri.durifront.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@RequestMapping("/signup")
@RestController
public class SignupController {

    private final SignupService signupService;
    private final SignupTempStorageService signupTempStorageService;

    // GET 요청 : 템플릿 반환
    // TODO: URL 경로 및 템플릿 경로 확인/수정
    @GetMapping("/step01")
    public ModelAndView getStep01() {
        return new ModelAndView("signup/step01");
    }

    @GetMapping("/step02")
    public ModelAndView getStep02() {
        return new ModelAndView("signup/step02");
    }

    @GetMapping("/step03")
    public ModelAndView getStep03() {
        return new ModelAndView("signup/step03");
    }

    @GetMapping("/step04")
    public ModelAndView getStep04() {
        return new ModelAndView("signup/step04");
    }

    @GetMapping("/step05")
    public ModelAndView getStep05() {
        return new ModelAndView("signup/step05");
    }


    // POST 요청 : 로직 수행
    // TODO: tempKey > 클라이언트 화면에서 UUID 생성 후 전달

    // Step01 : 성별
    @PostMapping("/step01")
    public ResponseEntity<String> signupStep01(
            @RequestParam String tempKey,
            @RequestBody SignupStep01RequestDto request
    )
    {
        signupTempStorageService.saveStep01(tempKey, request.gender());
        return ResponseEntity.ok("Step01 완료");
    }

    // Step02 : 얼굴 취향
    @PostMapping("/step02")
    public ResponseEntity<String> signupStep02(
            @RequestParam String tempKey,
            @RequestBody SignupStep02RequestDto request
    )
    {
        signupTempStorageService.saveStep02(tempKey, request.facePreference());
        return ResponseEntity.ok("Step02 완료");
    }

    // Step03 : 사용자 기본정보
    @PostMapping("/step03")
    public ResponseEntity<String> signupStep03(
            @RequestParam String tempKey,
            @RequestBody SignupStep03RequestDto request
            )
    {
        signupTempStorageService.saveStep03(
                tempKey,
                request.username(),
                request.password(),
                request.email()
        );

        return ResponseEntity.ok("Step03 완료");
    }

    // Step04 : 프로필 기본정보
    @PostMapping("/step04")
    public ResponseEntity<String> signupStep04(
            @RequestParam String tempKey,
            @RequestBody SignupStep04RequestDto request
            )
    {
        signupTempStorageService.saveStep04(
                tempKey,
                request.nickname(),
                request.birthDate(),
                request.region(),
                request.additionalInformation(),
                request.hobbies()
        );

        return ResponseEntity.ok("Step04 완료");
    }

    // Step05 : 아바타 생성 + 프로필(Profile) 생성
    @PostMapping("/step05")
    public ResponseEntity<String> signupStep05(
            @RequestParam String tempKey,
            @RequestBody SignupStep05RequestDto request
            )
    {
        // 1. Step05 데이터 임시 저장
        signupTempStorageService.saveStep05(
                tempKey,
                request.faceFeatures(),
                request.absoluteScore()
        );

        // 2. 누적된 모든 데이터를 가져와 User + Profile 생성 (트랜잭션)
        signupService.registerUserAndProfile(tempKey);

        // 3. 임시 데이터 제거
        signupTempStorageService.removeTempData(tempKey);

        return ResponseEntity.ok("회원가입 완료");
    }
}
