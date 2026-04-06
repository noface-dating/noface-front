package com.duri.durifront.web;

import jakarta.servlet.http.HttpSession;

public final class WebAuth {

    private WebAuth() {}

    public static boolean canEnterMain(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(WebSessionKeys.ONBOARDING_DONE))
                && Boolean.TRUE.equals(session.getAttribute(WebSessionKeys.SETUP_DONE));
    }

    public static String redirectUnlessMain(HttpSession session) {
        if (canEnterMain(session)) {
            return null;
        }
        if (Boolean.TRUE.equals(session.getAttribute(WebSessionKeys.ONBOARDING_DONE))) {
            return "redirect:/setup/avatar";
        }
        return "redirect:/onboarding";
    }

    public static String redirectUnlessCanUseSetup(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute(WebSessionKeys.SETUP_DONE))) {
            return "redirect:/";
        }
        if (Boolean.TRUE.equals(session.getAttribute(WebSessionKeys.ONBOARDING_DONE))) {
            return null;
        }
        if (session.getAttribute(WebSessionKeys.SIGNUP_USER_ID) != null) {
            return null;
        }
        return "redirect:/onboarding";
    }
}
