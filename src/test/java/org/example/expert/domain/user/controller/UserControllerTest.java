package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.TestObjectFactory;
import org.example.expert.config.UserDetailsImpl;
import org.example.expert.config.aop.UserRoleLogAspect;
import org.example.expert.config.filter.FilterConfig;
import org.example.expert.config.filter.MockTestFilter;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.admin.UserAdminServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {UserAdminController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = FilterConfig.class
                )
        }
)
@Import({ AopAutoConfiguration.class, UserRoleLogAspect.class }) // 사용할 Aop를 추가해준다.
public class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @MockBean
    private UserAdminServiceTest userAdminService;

    private UserRoleChangeRequest userRoleChangeRequest;
    private MockMvc mockMvc;
    private Principal mockPrincipal;

    @BeforeEach
    public void setUp() {
        // mockMvc에 사용할 Filter를 MockTestFilter로 지정
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity(new MockTestFilter()))
                .build();
    }

    /**
     * user 초기 설정하는 메서드
     */
    private void userSetup() {
        long userId = 1L;

        User testUser = TestObjectFactory.createUser(userId);
        testUser.updateRole(UserRole.ADMIN);
        UserDetailsImpl userDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 유저 권한 설정하는 메서드
     */
    private void userRoleSetup() {
        userRoleChangeRequest = new UserRoleChangeRequest(UserRole.ADMIN.name());
    }

    /**
     * User Controller에 changeUserRole 호출시 AOP가 정상작동 하는지 확인하는 메서드
     */
    @Test
    void AdminControllerAOPTest() throws Exception {
        // given
        userSetup();
        userRoleSetup();

        // userRoelChangeRequest를 Json형태로 변환
        String bodyContent = new ObjectMapper().writeValueAsString(userRoleChangeRequest);

        // when - then
        // Patch : /adin/users/{userId} 형태로 API호출
        mockMvc.perform(patch("/admin/users/{userId}", 1)
                        // RequestBody Type은 JSON 형태로 작성
                        .contentType(MediaType.APPLICATION_JSON)
                        // 내용
                        .content(bodyContent)
                        // 유저 정보
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
