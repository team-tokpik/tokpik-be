package org.example.tokpik_be.user.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.UserException;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.dto.response.UserProfileResponse;
import org.example.tokpik_be.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;

    public UserProfileResponse getUserProfile(long userId) {

        User user = findById(userId);
        String maskedEmail = maskEmail(user.getEmail());

        return new UserProfileResponse(maskedEmail, user.getProfilePhotoUrl());
    }

    /**
     * '@'를 기준으로 첫 부분, 첫 글자 제외한 나머지 글자 '*'로 마스킹 처리
     *
     * @param email 원래 이메일
     * @return 마스킹 처리된 이메일, ex) m*****@naver.com
     */
    private String maskEmail(String email) {

        StringBuilder result = new StringBuilder();
        String[] parts = email.split("@", 2);

        return result.append(parts[0].charAt(0))
            .append("*".repeat(parts[0].length() - 1))
            .append('@')
            .append(parts[1])
            .toString();
    }

    public User findById(long userId) {

        return userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
    }

    public boolean notExistByEmail(String email) {

        return !userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
            .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
    }

    public boolean notExistsById(long userId) {

        return !userRepository.existsById(userId);
    }
}
