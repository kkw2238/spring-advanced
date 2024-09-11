package org.example.expert.config.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.Protocol;
import org.example.expert.domain.user.enums.UserRole;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();

        // 로그인/회원가입의 경우 토큰 검증을 하지 않는다.
        if (url.startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        String bearerJwt = httpRequest.getHeader("Authorization");

        if (bearerJwt == null) {
            // 토큰이 없는 경우 400을 반환합니다.
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return;
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            // JWT 유효성 검사와 claims 추출
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            String userRoleString = claims.get(Protocol.USER_ROLE, String.class);
            UserRole userRole = UserRole.valueOf(userRoleString);

            httpRequest.setAttribute(Protocol.USER_ID, Long.parseLong(claims.getSubject()));
            httpRequest.setAttribute(Protocol.USER_EMAIL, claims.get(Protocol.USER_EMAIL));
            httpRequest.setAttribute(Protocol.USER_ROLE, userRoleString);

            /* 수정된 코드 : 이중 If문을 한 줄로 정리
             * Admin URL에 권한 없는 사람이 접근하는 상황 외에는 무조건 chain.doFilter를 호출 했기에
               해당 상황일 경우에만 체크하도록 변경
             */
            if (isAdminURL(url) && !isAdmin(userRole)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
                return;
            }

            chain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Invalid JWT token, 유효하지 않는 JWT 토큰 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않는 JWT 토큰입니다.");
        }
    }

    /** 추가된 코드 : URL이 Admin 관련 URL인지 확인하는 메서드
     * @param url 확인할 URL
     * @return True : Admin 관련 URL / False : 그 외의 URL
     */
    private boolean isAdminURL(String url) {
        return url.startsWith("/admin");
    }

    /**
     * 해당 유저 권한이 Admin인지 판별하는 메서드
     * @param userRole 유저 권한
     * @return True : 해당 권한이 ADMIN / False : 해당 권한이 ADMIN이 아닌 경우
     */
    private boolean isAdmin(UserRole userRole) {
        return UserRole.ADMIN.equals(userRole);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
