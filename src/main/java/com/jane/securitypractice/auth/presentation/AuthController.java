package com.jane.securitypractice.auth.presentation;

import com.jane.securitypractice.auth.application.AuthService;
import com.jane.securitypractice.auth.application.SessionManagementService;
import com.jane.securitypractice.auth.dto.UserRegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final SessionManagementService sessionManagementService;

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

    @GetMapping("/user/change-password")
    public String changePasswordPage() {
        return "user/change-password";
    }

    @PostMapping("/user/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Principal principal,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        try {
            authService.changePassword(username, currentPassword, newPassword);

            // 직접 로그아웃 처리
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }

            redirectAttributes.addFlashAttribute("success", "비밀번호가 변경되었습니다. 다시 로그인하세요.");
            return "redirect:/login"; // 비밀번호 변경 후 로그아웃 처리
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/change-password";
        }
    }

    // 내 세션 목록
    @GetMapping("/my-sessions")
    public String mySessionsPage(Model model, Authentication authentication) {
        List<SessionManagementService.MySessionInfoResponse> mySessions= sessionManagementService.getMySessions(authentication);
        model.addAttribute("sessions", mySessions);
        return "user/my-session-list";
    }

    @PostMapping("/my-sessions/expire/{sessionId}")
    @ResponseBody
    public String expireSession(@PathVariable String sessionId, Authentication authentication) {
        boolean result = sessionManagementService.expireMySession(sessionId, authentication);
        return result ? "세션 만료 완료" : "해당 세션을 찾을 수 없습니다.";
    }
}
