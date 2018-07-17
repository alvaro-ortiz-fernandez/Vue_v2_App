package com.vue.app.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {

    // for 403 access denied page
    @RequestMapping(value = "/denied", method = { RequestMethod.GET, RequestMethod.POST })
    public String accesssDenied(Model model) {
        // check if user is login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            model.addAttribute("username", userDetail.getUsername());
        }

        return "error/accessDenied";

    }
}
