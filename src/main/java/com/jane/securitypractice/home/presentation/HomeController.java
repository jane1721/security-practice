package com.jane.securitypractice.home.presentation;

import com.jane.securitypractice.auth.dto.UserRegisterDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String loginPage() {
        return "home/login";
    }

    @GetMapping("/session-expired")
    public String sessionExpired(Model model) {
        model.addAttribute("error", "다른 기기에서 로그인하여 현재 세션이 종료되었습니다.");
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
