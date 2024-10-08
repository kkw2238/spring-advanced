package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.TestObjectFactory;
import config.TestWebConfig;
import org.example.expert.config.filter.FilterConfig;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {UserController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = FilterConfig.class
                )
        }
)
public class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private UserService userService;
    private MockMvc mockMvc;
    private Principal mockPrincipal;

    @BeforeEach
    public void setUp() {
        mockMvc = TestWebConfig.setFilter(wac);
    }

    /**
     * user 초기 설정하는 메서드
     */
    private void userSetup(User user) {
        mockPrincipal = TestWebConfig.userSetup(user);
    }

    @Test
    void 유저_정보_조회에_성공한다() throws Exception {
        // given
        long userId = 1L;
        User user = TestObjectFactory.createUser(userId);

        userSetup(user);

        // when - then
        mockMvc.perform(get("/users/{userId}", userId)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void 비밀번호_변경에_성공한다() throws Exception {
        // given
        long userId = 1L;
        User user = TestObjectFactory.createUser(userId);
        userSetup(user);

        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest("1234", " 12345");
        String bodyContent = new ObjectMapper().writeValueAsString(userChangePasswordRequest);

        // when - then
        mockMvc.perform(put("/users")
                        // RequestBody Type은 JSON 형태로 작성
                        .contentType(MediaType.APPLICATION_JSON)
                        // 내용
                        .content(bodyContent)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService, times(1)).changePassword(eq(userId), any());
    }
}
