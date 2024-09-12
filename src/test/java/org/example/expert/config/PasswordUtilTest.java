package org.example.expert.config;

import org.example.expert.config.utility.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class PasswordUtilTest {
    @InjectMocks
    private PasswordUtil passwordUtil;

    @Test
    public void 비밀번호가_조건에_부합하면_False를_반환한다() {
        String password = "1234";

        boolean result = passwordUtil.isValidPassword(password);

        assertFalse(result);
    }

    @Test
    public void 비밀번호가_조건에_적합하면_True를_반환한다() {
        String password = "Aa123456";

        boolean result = passwordUtil.isValidPassword(password);

        assertTrue(result);
    }
}
