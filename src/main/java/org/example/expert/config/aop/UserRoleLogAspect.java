package org.example.expert.config.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class UserRoleLogAspect {
    /**
     * RoleLog라는 Annotation에게 반응하게 작성
     */
    @Pointcut("@annotation(org.example.expert.config.annotation.RoleLog)")
    public void adminLayer() { }

    /**
     * Admin 관련 메서드가 호출된 경우 Log를 출력해주는 메서드
     * @param pjp 메서드에 대한 정보가 담겨있는 ProceedingJoinPoint 객체
     * @param user 매개변수로 받아온 User정보
     * @return 메서드 결과 값
     */
    @Around(value = "adminLayer() && args(user, ..)")
    public Object adminLogMethodUsedUser(final ProceedingJoinPoint pjp, final AuthUser user) throws Throwable {
        HttpServletRequest request = findRequestInServlet(pjp.getArgs());

        // Request 객체가 발견되지 않은 경우 Error 메시지 출력
        if(request == null) {
            log.error("request is null");
        } else {
            ShowLog(user, request);
        }

        return pjp.proceed();
    }

    /**
     * Object[]에서 HttpServletRequest 관련 변수를 찾는 메서드
     * @param args ProceedingJoinPoint에서 추출해낸 매개변수들
     * @return Null : HttpServletRequest가 없는 경우 / 그 외 : 찾은 HttpServletRequest객체
     */
    private HttpServletRequest findRequestInServlet(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                return (HttpServletRequest) arg;
            }
        }

        return null;
    }

    /**
     * User 정보와 HttpServletRequest를 통해 로그를 출력하는 메서드
     * @param user 유저 정보
     * @param request URI 정보가 담겨져 있는 Request
     */
    private void ShowLog(AuthUser user, HttpServletRequest request) {
        log.info("::접근 ID : {}::", user.getId());
        log.info("::접근 권한 : {}", user.getUserRole());
        log.info("::접근 URL : {}::", request.getRequestURI());
        log.info("::접근 시각 : {}::", LocalDateTime.now());
    }
}
