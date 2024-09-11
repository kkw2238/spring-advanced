package org.example.expert.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.annotation.RoleLog;
import org.example.expert.config.aop.UserRoleLogAspect;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableAspectJAutoProxy
public class UserAdminController {

    private final ApplicationContext applicationContext;
    private final UserAdminService userAdminService;
    public UserAdminController(UserAdminService userAdminService, ApplicationContext applicationContext) {
        this.userAdminService = userAdminService;
        this.applicationContext = applicationContext;
    }

    @RoleLog
    @PatchMapping("/admin/users/{userId}")
    public void changeUserRole(@Auth AuthUser user, HttpServletRequest request, @PathVariable long userId, @RequestBody UserRoleChangeRequest userRoleChangeRequest) {
        userAdminService.changeUserRole(userId, userRoleChangeRequest);
    }
}
