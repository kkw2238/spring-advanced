package config;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.test.util.ReflectionTestUtils;

public class TestObjectFactory {
    public static AuthUser createAuthUser(Long id) {
        return new AuthUser(id, "email", UserRole.USER);
    }

    public static User createUser(Long id) {
        return User.fromAuthUser(createAuthUser(id));
    }

    public static Todo createTodo(User user) {
        return new Todo("Title", "contents", "Sunny", user);
    }

    public static Todo createTodo(User user, Long todoId) {
        Todo todo = createTodo(user);
        ReflectionTestUtils.setField(todo, "id", todoId);

        return todo;
    }

    public static Todo createTodo(TodoSaveRequest todoSaveRequest, User user, Long todoId) {
        return new Todo(todoSaveRequest.getTitle(), todoSaveRequest.getContents(), "Sunny", user);
    }

    public static Manager createManager(Todo todo) {
       return new Manager(todo.getUser(), todo);
    }

    public static Comment createComment(long userId) {
        User user = createUser(userId);
        Todo todo = createTodo(user);

        return new Comment("contents", user, todo);
    }

    public static Comment createComment(long userId, long commentId) {
        Comment comment = createComment(userId);
        ReflectionTestUtils.setField(comment, "id", commentId);

        return comment;
    };
}
