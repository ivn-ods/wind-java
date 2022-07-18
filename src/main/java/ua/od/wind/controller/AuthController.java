package ua.od.wind.controller;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.od.wind.model.User;
import ua.od.wind.service.UserService;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@Controller
public class AuthController {


    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
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
    public String doRegistration(@ModelAttribute("userForm") @Valid User userForm,
                                 BindingResult bindingResult) {

        if (userService.getOptionalUserByUsername(userForm.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "", "This login already in use");
        }

        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "", "Passwords is different");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.saveNewUser(userForm);
        return "redirect:/pay";
    }

    @GetMapping("/pay")
    public String pay(Principal principal,
                      Model model) {
        User readedUser = userService.getOptionalUserByUsername(principal.getName()).get();
        //update only-this-year payment id
        readedUser.setPaymentId(userService.getPaymentIdForThisYear(readedUser.getPaymentIdBase()));
        userService.saveUser(readedUser);

        String html = userService.getPaymentButton(readedUser);
        model.addAttribute("html", html);
        model.addAttribute("message", "Registration was successfull, " + readedUser.getUsername());

        return "pay";
    }

    @PostMapping("/callback")
    public String processPaymentCallback(@RequestParam(name = "data") String data, @RequestParam(name = "signature") String signature) throws ParseException, java.text.ParseException, IOException {
        String message = userService.processCallback(data, signature);
        return "redirect:/remember?message=" + message;
    }

    @GetMapping("/remember")
    public String rememberUser(@RequestParam(required = true) String message) {
        userService.doLogin();
        return "redirect:/?message=" + message;
    }


}
