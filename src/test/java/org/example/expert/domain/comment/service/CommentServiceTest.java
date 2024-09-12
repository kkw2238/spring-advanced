package org.example.expert.domain.comment.service;

import org.example.expert.TestObjectFactory;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
        // given
        long todoId = 1;
        long userId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = TestObjectFactory.createAuthUser(userId);

        given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            commentService.saveComment(authUser, todoId, request);
        });

        // then
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void comment를_정상적으로_등록한다() {
        // given
        long todoId = 1;
        long userId = 1;
        CommentSaveRequest request = new CommentSaveRequest("contents");
        AuthUser authUser = TestObjectFactory.createAuthUser(userId);
        User user = TestObjectFactory.createUser(userId);
        Todo todo = TestObjectFactory.createTodo(user);
        Comment comment = new Comment(request.getContents(), user, todo);

        given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(todo));
        given(commentRepository.save(any())).willReturn(comment);

        // when
        CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

        // then
        assertNotNull(result);
    }

    @Test
    public void 다수의_comment를_조회한다() {
        // given
        long todoId = 1;
        long userId = 1;

        User user = TestObjectFactory.createUser(userId);
        Todo todo = TestObjectFactory.createTodo(user);

        // comments 객체들
        List<Comment> comments = Arrays.asList(
                new Comment("contents", user, todo),
                new Comment("contents1", user, todo),
                new Comment("contents2", user, todo)
        );

        // findByIdWithUser호출시 comments가 반환되어야 한다.
        given(commentRepository.findByTodoIdWithUser(anyLong())).willReturn(comments);

        // when
        List<CommentResponse> result = commentService.getComments(todoId);

        // then
        assertNotNull(result);
    }
}
