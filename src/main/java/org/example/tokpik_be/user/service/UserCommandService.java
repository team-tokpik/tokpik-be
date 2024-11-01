package org.example.tokpik_be.user.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.type.domain.PlaceType;
import org.example.tokpik_be.type.domain.TopicType;
import org.example.tokpik_be.type.domain.UserPlaceType;
import org.example.tokpik_be.type.domain.UserTopicType;
import org.example.tokpik_be.type.repository.PlaceTypeRepository;
import org.example.tokpik_be.type.repository.TopicTypeRepository;
import org.example.tokpik_be.type.repository.UserPlaceTypeRepository;
import org.example.tokpik_be.type.repository.UserTopicTypeRepository;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.dto.request.UserMakeProfileRequest;
import org.example.tokpik_be.user.dto.request.UserUpdateNotificationTokenRequest;
import org.example.tokpik_be.user.enums.Gender;
import org.example.tokpik_be.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final TopicTypeRepository topicTypeRepository;
    private final PlaceTypeRepository placeTypeRepository;
    private final UserRepository userRepository;
    private final UserTopicTypeRepository userTopicTypeRepository;
    private final UserPlaceTypeRepository userPlaceTypeRepository;

    private final UserQueryService userQueryService;

    public User save(User user) {

        return userRepository.save(user);
    }

    public void makeProfile(long userId, UserMakeProfileRequest request) {
        User user = userQueryService.findById(userId);
        Gender gender = Gender.from(request.gender());
        user.updateProfile(request.birth(), gender);

        updateUserTopicTags(user, request.topicTagIds());
        updateUserPlaceTags(user, request.placeTagIds());
    }

    private void updateUserTopicTags(User user, List<Long> topicTagIds) {
        if (Objects.isNull(topicTagIds) || topicTagIds.isEmpty()) {

            return;
        }

        long userId = user.getId();
        List<TopicType> topicTypes = topicTypeRepository.findAllById(topicTagIds);
        if (topicTypes.isEmpty()) {

            return;
        }

        List<UserTopicType> userTopicTypes = topicTypes.stream()
            .map(topicTag -> new UserTopicType(userId, topicTag))
            .toList();
        userTopicTypeRepository.deleteByUserId(userId);
        userTopicTypeRepository.saveAll(userTopicTypes);
        user.updateUserTopicTags(userTopicTypes);
    }

    private void updateUserPlaceTags(User user, List<Long> placeTagIds) {
        if (Objects.isNull(placeTagIds) || placeTagIds.isEmpty()) {

            return;
        }

        long userId = user.getId();
        List<PlaceType> placeTypes = placeTypeRepository.findAllById(placeTagIds);
        if (placeTypes.isEmpty()) {

            return;
        }

        List<UserPlaceType> userPlaceTypes = placeTypes.stream()
            .map(placeTag -> new UserPlaceType(userId, placeTag))
            .toList();
        userPlaceTypeRepository.deleteByUserId(userId);
        userPlaceTypeRepository.saveAll(userPlaceTypes);
        user.updateUserPlaceTags(userPlaceTypes);
    }

    public void updateNotificationToken(long userId, UserUpdateNotificationTokenRequest request) {
        User user = userQueryService.findById(userId);

        String notificationToken = request.notificationToken();
        user.updateNotificationToken(notificationToken);
    }

    public void deleteUser(long userId) {
        User user = userQueryService.findById(userId);

        userRepository.delete(user);
    }
}
