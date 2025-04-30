package com.jane.securitypractice.user.controller;

import com.jane.securitypractice.user.domain.User;
import com.jane.securitypractice.user.dto.UserRegisterDto;
import com.jane.securitypractice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("user") UserRegisterDto userDto) { // form 바인딩 용 dto 객체를 미리 뷰에 전달
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") UserRegisterDto userDto, RedirectAttributes redirectAttributes) {

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "이미 사용 중인 아이디입니다.");
            return "redirect:/register";
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(List.of(userDto.getSelectedRole()))
                .build();

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인 해주세요.");
        return "redirect:/login";
    }
}
