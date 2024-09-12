package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.password.PasswordEncoder;
import org.example.expert.config.utility.PasswordUtil;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtil passwordUtil;

    /**
     * userId에 해당하는 유저 정보를 반환하는 메서드
     * @param userId 찾고자 하는 userId
     * @return 해당 userId를 갖고 있는 유저 객체
     */
    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        /* 수정된 코드 : 비밀번호 확인 메서드 분할
            비밀번호가 조건에 맞는지 확인
         */
        if (!passwordUtil.isValidPassword(userChangePasswordRequest.getNewPassword())) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }

        // 해당 userId를 가진 유저 판별
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        // 새 비밀번호와 현재 비밀번호 비교
        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        // 기존 비밀번호와 현재 비밀 번호가 맞는지 확인
        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        // 비밀번호 변경
        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }
}
