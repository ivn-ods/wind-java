package ua.od.wind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.od.wind.model.User;
import ua.od.wind.security.UserDetailsServiceImpl;

import javax.validation.Valid;

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
    public String getRegisterPage(@ModelAttribute("userForm") User userForm) {
        return "register";
    }

    @PostMapping("/register")
    public String doRegistration(@ModelAttribute("userForm")  @Valid User userForm, BindingResult bindingResult) {

        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
           // bindingResult.addError(new ObjectError("passwordConfirm", "Passwords is different"));
            bindingResult.rejectValue("passwordConfirm",  "Passwords is different");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }
        userDetailsServiceImpl.saveUser(userForm);
        // Authenticate
        //generate pay button

        return "ok";
    }
}
