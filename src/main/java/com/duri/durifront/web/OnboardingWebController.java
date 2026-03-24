package com.duri.durifront.web;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OnboardingWebController {

    private static final int TOTAL_FACE_ROUNDS = 10;

    @GetMapping("/onboarding")
    public String start() {
        return "onboarding/start";
    }

    @GetMapping("/onboarding/find-type")
    public String findType() {
        return "onboarding/find-type";
    }

    @GetMapping("/onboarding/face-preference")
    public String facePreference(
            @RequestParam(name = "gender", required = false) String gender, HttpSession session, Model model) {
        if (gender != null && !gender.isBlank()) {
            session.setAttribute(WebSessionKeys.FACE_GENDER, gender);
            session.removeAttribute(WebSessionKeys.FACE_CHOICES);
        }
        if (session.getAttribute(WebSessionKeys.FACE_GENDER) == null) {
            return "redirect:/onboarding/find-type";
        }
        @SuppressWarnings("unchecked")
        List<String> choices = (List<String>) session.getAttribute(WebSessionKeys.FACE_CHOICES);
        int n = choices == null ? 0 : choices.size();
        if (n >= TOTAL_FACE_ROUNDS) {
            return "redirect:/onboarding/preference-result";
        }
        model.addAttribute("roundDisplay", n + 1);
        model.addAttribute("totalRounds", TOTAL_FACE_ROUNDS);
        model.addAttribute("progressPct", (int) Math.round((n + 1) * (100.0 / TOTAL_FACE_ROUNDS)));
        return "onboarding/face-preference";
    }

    @PostMapping("/onboarding/face-preference")
    public String facePreferencePost(@RequestParam("choice") String choice, HttpSession session) {
        if (session.getAttribute(WebSessionKeys.FACE_GENDER) == null) {
            return "redirect:/onboarding/find-type";
        }
        @SuppressWarnings("unchecked")
        List<String> existing = (List<String>) session.getAttribute(WebSessionKeys.FACE_CHOICES);
        List<String> choices = existing == null ? new ArrayList<>() : new ArrayList<>(existing);
        choices.add(choice);
        session.setAttribute(WebSessionKeys.FACE_CHOICES, choices);
        if (choices.size() >= TOTAL_FACE_ROUNDS) {
            return "redirect:/onboarding/preference-result";
        }
        return "redirect:/onboarding/face-preference";
    }

    @GetMapping("/onboarding/preference-result")
    public String preferenceResult() {
        return "onboarding/preference-result";
    }
}
