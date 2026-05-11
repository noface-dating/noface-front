package com.duri.durifront.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Value("${gateway.server.url}")
    private String gatewayServerUrl;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("gatewayServerUrl", gatewayServerUrl);
        return "auth/login";
    }
}
