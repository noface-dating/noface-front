package com.duri.durifront.web;

import com.duri.durifront.avatar.service.AvatarService;
import com.duri.durifront.handphoto.service.HandPhotoService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SetupWebController {

    private static final int MAX_INTERESTS = 7;

    private final AvatarService avatarService;
    private final HandPhotoService handPhotoService;

    @GetMapping("/setup/avatar")
    public String avatarForm(HttpSession session) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        return "setup/avatar";
    }

    @PostMapping("/setup/avatar")
    public String avatarSubmit(
            @RequestParam("face") MultipartFile face,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        try {
            var result = avatarService.generate(face);
            redirectAttributes.addFlashAttribute("avatarUrl", result.avatarUrl());
            return "redirect:/setup/avatar/result";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/setup/avatar";
        }
    }

    @GetMapping("/setup/avatar/result")
    public String avatarResult(Model model) {
        if (model.asMap().get("avatarUrl") == null) {
            return "redirect:/setup/avatar";
        }
        return "setup/avatar-result";
    }

    @GetMapping("/setup/hand")
    public String handForm(HttpSession session) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        return "setup/hand";
    }

    @GetMapping("/setup/hand/success")
    public String handSuccess(HttpSession session) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        return "setup/hand-success";
    }

    @PostMapping("/setup/hand")
    public String handSubmit(
            @RequestParam("image") MultipartFile image,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        try {
            handPhotoService.upload(image);
            return "redirect:/setup/hand/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/setup/hand";
        }
    }

    @GetMapping("/setup/interests")
    public String interestsForm(HttpSession session, Model model) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        model.addAttribute("categories", InterestCatalog.ALL);
        model.addAttribute("maxSelection", MAX_INTERESTS);
        return "setup/interests";
    }

    @PostMapping("/setup/interests")
    public String interestsSubmit(
            @RequestParam(value = "interests", required = false) List<String> interests,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        int n = interests == null ? 0 : interests.size();
        if (n > MAX_INTERESTS) {
            redirectAttributes.addFlashAttribute("error", "관심사는 최대 " + MAX_INTERESTS + "개까지 선택할 수 있어요.");
            return "redirect:/setup/interests";
        }
        session.setAttribute(WebSessionKeys.ONBOARDING_DONE, true);
        session.setAttribute(WebSessionKeys.SETUP_DONE, true);
        session.removeAttribute(WebSessionKeys.SIGNUP_USER_ID);
        session.removeAttribute(WebSessionKeys.SIGNUP_PASSWORD);
        return "redirect:/";
    }

    @GetMapping("/setup/profile")
    public String setupProfile(HttpSession session) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        return "setup/profile";
    }

    @PostMapping("/setup/profile")
    public String setupProfileSubmit(HttpSession session) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        return "redirect:/setup/interests";
    }

    @GetMapping("/setup/discovery")
    public String setupDiscovery(HttpSession session) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        return "setup/discovery";
    }

    @PostMapping("/setup/discovery")
    public String setupDiscoverySubmit(HttpSession session) {
        String r = WebAuth.redirectUnlessCanUseSetup(session);
        if (r != null) {
            return r;
        }
        session.setAttribute(WebSessionKeys.ONBOARDING_DONE, true);
        session.setAttribute(WebSessionKeys.SETUP_DONE, true);
        return "redirect:/";
    }
}
