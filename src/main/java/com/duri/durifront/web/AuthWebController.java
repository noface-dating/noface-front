/**
 * @author kyw10987
 *
 * Ambiguous mapping 진규씨^^
 */

//package com.duri.durifront.web;
//
//import jakarta.servlet.http.HttpSession;
//import java.util.stream.IntStream;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//public class AuthWebController {
//
//    private static final java.util.Set<String> TAKEN_IDS =
//            java.util.Set.of("admin", "test", "hide");
//
//    @GetMapping("/login")
//    public String loginForm() {
//        return "auth/login";
//    }
//
//    @PostMapping("/login")
//    public String loginSubmit(HttpSession session) {
//        session.setAttribute(WebSessionKeys.ONBOARDING_DONE, true);
//        return "redirect:/setup/avatar";
//    }
//
//    @GetMapping("/signup")
//    public String signupForm() {
//        return "auth/signup";
//    }
//
//    /**
//     * 데모: 입력 없이「가입하기」만 눌러도 다음 단계로 진행 (빈 값은 기본 데모 계정으로 채움).
//     */
//    @PostMapping("/signup")
//    public String signupSubmit(
//            @RequestParam(required = false) String userId,
//            @RequestParam(required = false) String password,
//            @RequestParam(required = false) String passwordConfirm,
//            @RequestParam(required = false) Boolean agree1,
//            @RequestParam(required = false) Boolean agree2,
//            HttpSession session,
//            RedirectAttributes ra) {
//        String id = userId == null || userId.isBlank() ? "demo" : userId.trim();
//        if (id.length() < 2) {
//            id = "demo";
//        }
//        if (TAKEN_IDS.contains(id.toLowerCase())) {
//            ra.addFlashAttribute("error", "이미 사용 중인 아이디예요");
//            return "redirect:/signup";
//        }
//        String pw = password == null ? "" : password;
//        String pwc = passwordConfirm == null ? "" : passwordConfirm;
//        if (!pw.isEmpty() && !pw.equals(pwc)) {
//            ra.addFlashAttribute("error", "비밀번호가 일치하지 않습니다");
//            return "redirect:/signup";
//        }
//        session.setAttribute(WebSessionKeys.SIGNUP_USER_ID, id);
//        session.setAttribute(WebSessionKeys.SIGNUP_PASSWORD, pw);
//        return "redirect:/signup/profile-info";
//    }
//
//    @GetMapping("/signup/profile-info")
//    public String signupProfileInfo(HttpSession session, Model model) {
//        if (session.getAttribute(WebSessionKeys.SIGNUP_USER_ID) == null) {
//            return "redirect:/signup";
//        }
//        int cy = java.time.Year.now().getValue();
//        model.addAttribute("years", IntStream.rangeClosed(cy - 79, cy).boxed().toList());
//        model.addAttribute("months", IntStream.rangeClosed(1, 12).boxed().toList());
//        model.addAttribute("days", IntStream.rangeClosed(1, 31).boxed().toList());
//        return "auth/signup-profile";
//    }
//
//    @PostMapping("/signup/profile-info")
//    public String signupProfileSubmit(HttpSession session) {
//        if (session.getAttribute(WebSessionKeys.SIGNUP_USER_ID) == null) {
//            return "redirect:/signup";
//        }
//        return "redirect:/setup/avatar";
//    }
//}
