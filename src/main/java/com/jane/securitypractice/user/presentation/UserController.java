package com.jane.securitypractice.user.presentation;

import com.jane.securitypractice.auth.infrastructure.CustomUserDetails;
import com.jane.securitypractice.user.domain.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    public String userPage() {
        return "user/user";
    }

    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("roles", userDetails.getAuthorities());
        model.addAttribute("aboutMe", userDetails.getUser().getAboutMe());

        return "user/profile";
    }

    // 프로필 수정 페이지
    @GetMapping("/profile/edit")
    public String editProfilePage(Authentication authentication, Model model) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("roles", userDetails.getAuthorities());
        model.addAttribute("aboutMe", userDetails.getUser().getAboutMe());

        return "user/profile-edit";
    }

    // 프로필 수정
    @PostMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileUpdateRequest request, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        userService.updateUserAboutMe(authentication, userDetails.getUsername(), request.aboutMe);
        return ResponseEntity.ok("Updated");
    }

    @Getter @Setter
    static class ProfileUpdateRequest {
        private String aboutMe;
    }
}
