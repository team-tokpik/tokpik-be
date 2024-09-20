package org.example.tokpik_be.user.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.dto.request.UserMakeProfileRequest;
import org.example.tokpik_be.user.enums.Gender;
import org.example.tokpik_be.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;

    public User save(User user) {

        return userRepository.save(user);
    }

    public void makeProfile(long userId, UserMakeProfileRequest request) {
        User user = userQueryService.findById(userId);
        Gender gender = Gender.from(request.gender());
        user.updateProfile(request.birth(), gender);
    }
}
