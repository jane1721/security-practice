package com.jane.securitypractice.home.presentation;

import com.jane.securitypractice.auth.dto.UserRegisterDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String loginPage() {
        return "home/login";
    }

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("user") UserRegisterDto userDto) { // form 바인딩 용 dto 객체를 미리 뷰에 전달
        return "home/register";
    }

    @GetMapping("/")
    public String homePage() {
        return "home/home";
    }

    @GetMapping("/access-denied")
    public String deniedPage() {
        return "home/access-denied";
    }
}
