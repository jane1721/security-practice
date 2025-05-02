package com.jane.securitypractice.user.presentation;

import com.jane.securitypractice.user.domain.Role;
import com.jane.securitypractice.user.dto.UserDto;
import com.jane.securitypractice.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

    private final UserService userService;

    /// 사용자 목록 화면
    @PreAuthorize("hasRole('ADMIN')") // URL 패턴으로 SecurityConfig 에서 접근 제어하고 있으므로 @PreAuthorize 는 선택 사항
    @GetMapping("/users")
    public String userListPage(Model model) {

        List<UserDto> users = userService.getAllUsers();
        model.addAttribute("users", users);

        return "admin/user-list";
    }

    // 역할 수정 폼
    @GetMapping("/users/{id}/edit-role")
    public String editUserRole(@PathVariable Long id, Model model) {

        UserDto user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", EnumSet.allOf(Role.class));

        return "admin/edit-role";
    }

    // 역할 수정 처리
    @PostMapping("/users/{id}/edit-role")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam("roles") List<Role> roles,
                                 RedirectAttributes redirectAttributes) {
        userService.updateUserRoles(id, Set.copyOf(roles));
        redirectAttributes.addFlashAttribute("success", "사용자 권한이 변경되었습니다.");
        return "redirect:/admin/users";
    }
}
