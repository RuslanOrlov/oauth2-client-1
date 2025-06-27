package org.oauth2client120250105.controllers;

import lombok.RequiredArgsConstructor;
import org.oauth2client120250105.services.CheckConsentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CheckConsentController {

    private final CheckConsentService checkConsentService;

    @GetMapping("/check-consent")
    public String checkConsent() {
        if (checkConsentService.isConsentRemoved()) {
            return "redirect:/my-logout?consentRemoved=true";
        }
        return "redirect:/";
    }

}
