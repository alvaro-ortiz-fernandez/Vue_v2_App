package com.vue.app.web;

import com.vue.app.repo.model.User;
import com.vue.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        model.addAttribute("error", error != null);
        model.addAttribute("msg", logout != null);

        return "login/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "success", required = false) String success,
                        Model model) {

        model.addAttribute("error", error != null);
        model.addAttribute("success", success != null);

        return "login/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public RedirectView registerForm(@ModelAttribute("userDTO") User user) {
        if (userService.exists(user.getUsername())) {
            return new RedirectView("register?error");
        } else {
            userService.register(user);
            return new RedirectView("register?success");
        }
    }
}