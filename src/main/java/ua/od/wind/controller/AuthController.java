package ua.od.wind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.od.wind.model.User;
import ua.od.wind.security.UserDetailsServiceImpl;

@Controller
public class AuthController {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public AuthController(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping("login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegistration(@ModelAttribute("userForm") User userForm, Model model) {

        userDetailsServiceImpl.saveUser(userForm);
        return "ok";
    }
}
