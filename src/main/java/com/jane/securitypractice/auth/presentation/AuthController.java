package com.jane.securitypractice.auth.presentation;

import com.jane.securitypractice.auth.application.AuthService;
import com.jane.securitypractice.auth.dto.UserRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("user") UserRegisterDto userDto) { // form 바인딩 용 dto 객체를 미리 뷰에 전달
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegisterDto userDto, RedirectAttributes redirectAttributes) {

        if (authService.isUsernameDuplicate(userDto.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
            return "redirect:/register";
        }

        authService.registerUser(userDto);

        redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인 해주세요.");
        return "redirect:/login";
    }
}
