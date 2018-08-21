package com.vue.app.web;

import com.vue.app.repo.model.User;
import com.vue.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    // La app sólo tiene 1 controlador por GET (las rutas se gestionan en el front con Vue),
    // si se intenta acceder a otra ruta redirigimos a '/'
    @RequestMapping(value = { "/*" }, method = RequestMethod.GET)
    public String anyother(Model model, Principal principal, HttpServletRequest request) {
        return "redirect:/";
    }

    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public String app(Model model, Principal principal, HttpServletRequest request) {

        User user = principal == null ? null : userService.toPublicUser(userService.findById(principal.getName()).get());

        model.addAttribute("user", user);
        return "app/app";
    }
}