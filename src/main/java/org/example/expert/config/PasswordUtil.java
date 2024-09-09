package org.example.expert.config;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordUtil {
    // 비밀번호는 소문자, 숫자, 대문자가 필수로 들어가야 하는 8글자 이상의 문자열이다.
    private final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$";

    /**
     * 비밀번호가 조건에 맞는지 확인하는 메서드
     * @param password 작성된 비밀번호
     * @return True : 조건에 맞는 비밀번호 / False : 조건에 부합한 비밀번호
     */
    public boolean isValidPassword(String password) {
        Pattern passwordPattern = Pattern.compile(REGEX);

        return passwordPattern.matcher(REGEX).matches();
    }
}
