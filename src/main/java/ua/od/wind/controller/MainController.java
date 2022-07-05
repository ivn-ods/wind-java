package ua.od.wind.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/main")
    public String getMainPage() {
        return "main";
    }

    @GetMapping("/shifted")
    public String getShiftedPage() {
        return "shifted";
    }
}
