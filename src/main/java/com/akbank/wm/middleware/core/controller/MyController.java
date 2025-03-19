package com.akbank.wm.middleware.core.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {

    @Value("${domainUrl}")
    private String domainUrl;

    @GetMapping("/monitor")
    public String monitor(Model model) {

        model.addAttribute("domainUrl", domainUrl);
        return "monitor";

    }
}
