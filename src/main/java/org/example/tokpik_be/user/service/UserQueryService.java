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

        return UserProfileResponse.from(user);
    }

    public User findById(long userId) {

        return userRepository.findById(userId)
            .orElseThrow(() -> new GeneralException(UserException.USER_NOT_FOUND));
    }
}
