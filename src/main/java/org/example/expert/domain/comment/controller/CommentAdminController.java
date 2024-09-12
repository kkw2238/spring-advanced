package org.example.expert.domain.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.comment.service.CommentAdminService;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.annotation.RoleLog;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentAdminService commentAdminService;

    @RoleLog
    @DeleteMapping("/admin/comments/{commentId}")
    public void deleteComment(@Auth AuthUser user, HttpServletRequest httpServletRequest, @PathVariable long commentId) {
        commentAdminService.deleteComment(commentId);
    }
}
