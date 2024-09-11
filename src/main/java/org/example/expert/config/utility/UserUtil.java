package org.example.expert.config.utility;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private final String BOUNDARY = "abcdefghijklmnopqrstuvwxyz0123456789";

    private String createRandomEmail() {

    }

    public User createRandomUser(UserRole userRole) {
        User user = new User();


    }
}
