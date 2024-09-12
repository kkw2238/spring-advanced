package org.example.expert.domain.manager.service;

import config.TestObjectFactory;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerServiceDeleteTest {
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private ManagerService managerService;

    @Test
    public void manager삭제시_User가_존재하지_않는다면_IRE를_반환한다() {
        long todoId = 1L;
        long userId = 1L;
        long managerId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(userId, todoId, managerId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void manager삭제시_Todo의_유저Id와_User의_유저Id가_다르다면_IRE를_반환한다() {
        long todoId = 1L;
        long userId = 1L;
        long todoUserId = 2L;
        long managerId = 1L;

        User user = TestObjectFactory.createUser(userId);
        User todoUser = TestObjectFactory.createUser(todoUserId);
        Todo todo = TestObjectFactory.createTodo(todoUser, todoId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(userId, todoId, managerId));

        assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    public void manager삭제시_Manager가_존재하지_않는다면_IRE를_반환한다() {
        long todoId = 1L;
        long userId = 1L;
        long managerId = 1L;

        User user = TestObjectFactory.createUser(userId);
        Todo todo = TestObjectFactory.createTodo(user, todoId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findById(managerId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(userId, todoId, managerId));

        assertEquals("Manager not found", exception.getMessage());
    }

    @Test
    public void manager삭제시_Manager가_해당_Todo관리자가_아닐경우_IRE를_반환한다() {
        long todoId = 1L;
        long anotherTodoId = 2L;
        long userId = 1L;
        long managerId = 1L;

        User user = TestObjectFactory.createUser(userId);
        Todo todo = TestObjectFactory.createTodo(user, todoId);
        Todo anotherTodo = TestObjectFactory.createTodo(user, anotherTodoId);
        Manager manager = TestObjectFactory.createManager(anotherTodo);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> managerService.deleteManager(userId, todoId, managerId));

        assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());
    }

    @Test
    public void 성공적으로_manager삭제를_한다() {
        long todoId = 1L;
        long userId = 1L;
        long managerId = 1L;

        User user = TestObjectFactory.createUser(userId);
        Todo todo = TestObjectFactory.createTodo(user, todoId);
        Manager manager = TestObjectFactory.createManager(todo);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
        given(managerRepository.findById(managerId)).willReturn(Optional.of(manager));

        // when & then
        managerService.deleteManager(userId, todoId, managerId);

        verify(managerRepository, times(1)).delete(any());
    }
}
