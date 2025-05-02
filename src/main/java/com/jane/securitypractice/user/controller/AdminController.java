package com.jane.securitypractice.user.controller;

import com.jane.securitypractice.user.dto.UserDto;
import com.jane.securitypractice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    /// 사용자 목록 화면
    @PreAuthorize("hasRole('ADMIN')") // URL 패턴으로 SecurityConfig 에서 접근 제어하고 있으므로 @PreAuthorize 는 선택 사항
    @GetMapping("/users")
    public String userListPage(Model model) {

        List<UserDto> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "admin/user-list";
    }
}
