package com.jane.securitypractice.auth.presentation;

import com.jane.securitypractice.auth.application.SessionManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminAuthController {
    private final SessionManagementService sessionManagementService;

    @GetMapping("/session-list")
    public String sessionListPage(Model model) {
        List<SessionManagementService.SessionInfoResponse> sessions = sessionManagementService.getAllLoggedInUsers();
        model.addAttribute("sessions", sessions);
        return "admin/session-list";
    }
}
